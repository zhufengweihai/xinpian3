package uni.zf.xinpian.download

import android.app.Notification
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Requirements.RequirementFlags
import androidx.media3.exoplayer.scheduler.Scheduler
import okhttp3.Cache
import okhttp3.OkHttpClient
import uni.zf.xinpian.App
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import androidx.media3.exoplayer.offline.DownloadService.DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL as INTERVAL
import uni.zf.xinpian.R.drawable.ic_download as ic
import uni.zf.xinpian.R.string.channel_name as name

@OptIn(UnstableApi::class)
class VideoDownloadService : DownloadService(NOTIFICATION_ID, INTERVAL, CHANNEL_ID, name, 0) {

    override fun getDownloadManager(): DownloadManager {
        return Companion.downloadManager
    }

    override fun getScheduler(): Scheduler {
        return PlatformScheduler(this, JOB_ID)
    }

    override fun getForegroundNotification(downloads: List<Download>, nmr: @RequirementFlags Int): Notification {
        val helper = DownloadNotificationHelper(this, CHANNEL_ID)
        return helper.buildProgressNotification(this, ic, null, null, downloads, nmr)
    }

    companion object {
        private const val JOB_ID = 1
        private const val NOTIFICATION_ID = FOREGROUND_NOTIFICATION_ID_NONE
        private const val CHANNEL_ID: String = "download_channel"
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        val downloadManager: DownloadManager by lazy { createDownloadManager() }
        val dataSourceFactory: DataSource.Factory by lazy { getHttpDataSourceFactory(App.INSTANCE.applicationContext) }

        private fun createDownloadManager(): DownloadManager {
            val context = App.INSTANCE.applicationContext
            val databaseProvider = StandaloneDatabaseProvider(context)
            val downloadContentDirectory = File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
            val downloadCache = SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), databaseProvider)
            return DownloadManager(
                context,
                databaseProvider,
                downloadCache,
                dataSourceFactory,
                Executors.newFixedThreadPool(6)
            )
        }

        private fun getHttpDataSourceFactory(context: Context): DataSource.Factory {
            val cache = Cache(File(context.cacheDir, "exo_cache"), 100 * 1024 * 1024)
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .cache(cache)
                .build()
            return OkHttpDataSource.Factory(okHttpClient)
            /*if (Build.VERSION.SDK_INT >= 30 && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 7) {
                val httpEngine = HttpEngine.Builder(context).build()
                return HttpEngineDataSource.Factory(httpEngine, Executors.newSingleThreadExecutor())
            }

            val cronetEngine = CronetUtil.buildCronetEngine(context)
            if (cronetEngine != null) {
                return CronetDataSource.Factory(cronetEngine, Executors.newSingleThreadExecutor())
            }

            val cookieManager = CookieManager().apply {
                setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
            }
            CookieHandler.setDefault(cookieManager)
            return DefaultHttpDataSource.Factory()*/
        }

        private fun getDownloadDirectory(context: Context): File {
            return context.getExternalFilesDir(null) ?: context.filesDir
        }
    }
}