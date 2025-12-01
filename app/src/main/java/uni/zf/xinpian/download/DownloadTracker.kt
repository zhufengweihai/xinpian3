package uni.zf.xinpian.download

import android.content.DialogInterface
import android.net.Uri
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.alibaba.fastjson.JSON
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uni.zf.xinpian.App
import uni.zf.xinpian.R
import java.io.IOException
import androidx.core.net.toUri
import androidx.media3.exoplayer.offline.DownloadService.sendRemoveDownload

@UnstableApi
object DownloadTracker {
    private val downloadManager: DownloadManager = VideoDownloadService.downloadManager
    val dataSourceFactory: DataSource.Factory by lazy { VideoDownloadService.dataSourceFactory}
    private val downloads = mutableMapOf<Uri, Download>()
    private var startDownloadDialogHelper: StartDownloadDialogHelper? = null

    init {
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }

    fun getCurrentDownloads(): MutableList<Download> = downloadManager.currentDownloads

    fun isDownloaded(uri: Uri): Boolean = downloads[uri]?.state != Download.STATE_FAILED

    fun getDownloadRequest(uri: Uri): DownloadRequest? =
        downloads[uri]?.takeIf { it.state != Download.STATE_FAILED }?.request

    fun startDownload(items: List<MediaItem>) {
        items.forEach { startDownload(it) }
    }

    fun startDownload(item: MediaItem) {
        downloads[item.localConfiguration?.uri]?.let {
            if (it.state != Download.STATE_FAILED) return
        }

        startDownloadDialogHelper?.release()
        val context = App.INSTANCE.applicationContext
        val helper = DownloadHelper.forMediaItem(context, item, null, dataSourceFactory)
        startDownloadDialogHelper = StartDownloadDialogHelper(helper, item)
    }

    fun pauseDownload(url: String) {
        setStopReason(url.toUri(), Download.STOP_REASON_NONE + 1)
    }

    fun pauseDownload(uri: Uri) {
        setStopReason(uri, Download.STOP_REASON_NONE + 1)
    }

    fun pauseAllDownloads(urls: List<String>) {
        urls.forEach { setStopReason(it.toUri(), Download.STOP_REASON_NONE + 1) }
    }

    fun pauseAllDownloads() {
        DownloadService.sendSetStopReason(
            App.INSTANCE.applicationContext,
            VideoDownloadService::class.java,
            null,
            Download.STOP_REASON_NONE + 1,
            false
        )
    }

    fun resumeDownload(url: String) {
        setStopReason(url.toUri(), Download.STOP_REASON_NONE)
    }

    fun resumeAllDownloads(urls: List<String>) {
        urls.forEach { setStopReason(it.toUri(), Download.STOP_REASON_NONE) }
    }

    fun resumeAllDownloads() {
        val context = App.INSTANCE.applicationContext
        downloadManager.currentDownloads.forEach {
            DownloadService.sendSetStopReason(
                context,
                VideoDownloadService::class.java,
                it.request.id,
                Download.STOP_REASON_NONE,
                false
            )
        }
    }

    fun removeAllDownloads() {
        downloadManager.removeAllDownloads()
    }

    fun removeDownload(url: String) {
        downloads[url.toUri()]?.let {
            val context = App.INSTANCE.applicationContext
            sendRemoveDownload(context, VideoDownloadService::class.java, it.request.id, false)
        }
    }

    fun removeDownloads(urls: List<String>) {
        urls.forEach { url ->
            downloads[url.toUri()]?.let {
                val context = App.INSTANCE.applicationContext
                sendRemoveDownload(context, VideoDownloadService::class.java, it.request.id, false)
            }
        }
    }

    fun removeDownloadsWithout(urls: List<String>) {
        val context = App.INSTANCE.applicationContext
        downloads.filterKeys { it.toString() !in urls }.values.forEach {
            sendRemoveDownload(context, VideoDownloadService::class.java, it.request.id, false)
        }
    }

    private fun loadDownloads() {
        try {
            downloadManager.downloadIndex.getDownloads().use { cursor ->
                while (cursor.moveToNext()) {
                    val download = cursor.download
                    downloads[download.request.uri] = download
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to query downloads: ${e.message}", e)
        }
    }

    private fun setStopReason(uri: Uri, stopReason: Int) {
        downloads[uri]?.let {
            DownloadService.sendSetStopReason(
                App.INSTANCE.applicationContext,
                VideoDownloadService::class.java,
                it.request.id,
                stopReason,
                false
            )
        }
    }

    private class DownloadManagerListener : DownloadManager.Listener {
        override fun onDownloadChanged(dm: DownloadManager, download: Download, e: Exception?) {
            downloads[download.request.uri] = download
            CoroutineScope(Dispatchers.IO).launch {
                val dao = App.INSTANCE.appDb.downloadDao()
                dao.updateItem(
                    download.request.uri.toString(),
                    download.state,
                    download.bytesDownloaded,
                    download.percentDownloaded
                )
            }
        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            downloads.remove(download.request.uri)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = App.INSTANCE.appDb.downloadDao()
                dao.deleteItemThenDeleteVideo(download.request.uri.toString())
            }
        }
    }

    private class StartDownloadDialogHelper(
        private val downloadHelper: DownloadHelper,
        private val mediaItem: MediaItem
    ) : DownloadHelper.Callback, DialogInterface.OnDismissListener {

        init {
            downloadHelper.prepare(this)
        }

        fun release() {
            downloadHelper.release()
        }

        override fun onPrepared(helper: DownloadHelper, tracksInfoAvailable: Boolean) {
            onDownloadPrepared()
        }

        override fun onPrepareError(helper: DownloadHelper, e: IOException) {
            val context = App.INSTANCE.applicationContext
            Toast.makeText(context, R.string.download_start_error, Toast.LENGTH_LONG).show()
            Log.e(TAG, "Failed to start download", e)
        }

        override fun onDismiss(dialogInterface: DialogInterface) {
            downloadHelper.release()
        }

        private fun onDownloadPrepared() {
            startDownload()
            downloadHelper.release()
        }

        private fun startDownload(downloadRequest: DownloadRequest = buildDownloadRequest()) {
            val context = App.INSTANCE.applicationContext
            DownloadService.sendAddDownload(context, VideoDownloadService::class.java, downloadRequest, false)
        }

        private fun buildDownloadRequest(): DownloadRequest {
            return downloadHelper.getDownloadRequest(JSON.toJSONBytes(mediaItem))
        }
    }

    private const val TAG = "DownloadTracker"
}