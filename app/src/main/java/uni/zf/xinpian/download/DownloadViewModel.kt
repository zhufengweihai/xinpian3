package uni.zf.xinpian.download

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.launch
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo

@OptIn(UnstableApi::class)
class DownloadViewModel : ViewModel() {
    private val downloadDao = App.INSTANCE.appDb.downloadDao()

    fun insertVideo(video: DownloadVideo) = performDatabaseOperation { downloadDao.insertVideo(video) }

    fun insertItem(item: DownloadItem) = performDatabaseOperation { downloadDao.insertItem(item) }

    fun deleteVideo(video: DownloadVideo) = performDatabaseOperation { downloadDao.deleteVideo(video) }

    fun getAllVideos() = downloadDao.getAllVideos()

    fun getAllMedias() = downloadDao.getAllMedias()

    fun getAllItems(_id: String) = downloadDao.getAllItems(_id)

    fun deleteAll() = DownloadTracker.removeAllDownloads()

    suspend fun deleteAllItemsByVideoId(_id: String) {
        val urls = downloadDao.getItemsByVideoId(_id)
        DownloadTracker.removeDownloads(urls)
    }

    fun pauseAllDownloads() = DownloadTracker.pauseAllDownloads()

    suspend fun pauseDownloads(videoId: String) {
        val urls = downloadDao.getItemsByVideoId(videoId)
        DownloadTracker.pauseAllDownloads(urls)
    }

    suspend fun resumeDownloads(videoId: String) {
        val urls = downloadDao.getItemsByVideoId(videoId)
        DownloadTracker.resumeAllDownloads(urls)
    }


    fun resumeAllDownloads() = DownloadTracker.resumeAllDownloads()

    fun deleteItem(url: String) = DownloadTracker.removeDownload(url)

    fun pauseDownloadItem(url: String) = DownloadTracker.pauseDownload(url)

    fun resumeDownload(url: String) = DownloadTracker.resumeDownload(url)

    suspend fun deleteAllCache() {
        val urls = downloadDao.getAllDownloadUrls()
        DownloadTracker.removeDownloadsWithout(urls)
    }

    private fun performDatabaseOperation(operation: suspend () -> Unit) {
        viewModelScope.launch { operation() }
    }
}