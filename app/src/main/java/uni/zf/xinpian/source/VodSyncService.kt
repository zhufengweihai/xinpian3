package uni.zf.xinpian.source

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uni.zf.xinpian.App

private const val INDEX_ID = 4
private const val MAX_PAGINATION = 10000
const val SYNC_INTERVAL = 30 * 60 * 1000L
private const val MILLI_OF_24_HOURS = 24 * 60 * 60 * 1000

const val EXTRA_VOD_ID = "vodIds"
fun startVodSync(context: Context) {
    context.startService(Intent(context, VodSyncService::class.java))
}

fun startVodSync(context: Context, vodIdList: ArrayList<String>) {
    context.startService(Intent(context, VodSyncService::class.java).putStringArrayListExtra(EXTRA_VOD_ID, vodIdList))
}

class VodSyncService : Service() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val preferences by lazy { getSharedPreferences("xinpian", MODE_PRIVATE) }
    private val videoDao by lazy { App.INSTANCE.appDb.videoDao() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val vodIds = intent?.getStringArrayListExtra(EXTRA_VOD_ID)
        vodIds?.let { syncVods(it) } ?: run { syncLatestVideoVods() }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun syncVods(vodIdList: ArrayList<String>) = vodIdList.forEach { syncVod(it) }

    private fun syncVod(vodId: String) = serviceScope.launch {
        val service = getVodSource(vodId.substring(0, INDEX_ID))
        val ids = listOf(vodId.substring(INDEX_ID))
        service.requestVodVideo(ids)?.let { videoDao.insertVideoFromVod(it) }
    }

    private fun syncLatestVideoVods() {
        serviceScope.launch { updateVideoFromVod(BfzySource()) }
        serviceScope.launch { updateVideoFromVod(DyzySource()) }
        serviceScope.launch { updateVideoFromVod(FfzySource()) }
    }

    private suspend fun updateVideoFromVod(vodService: VodSource) {
        val currentTime = System.currentTimeMillis()
        val lastUpdate = preferences.getLong(vodService.prefUpdate, currentTime - MILLI_OF_24_HOURS)
        if (currentTime - lastUpdate < SYNC_INTERVAL) return
        preferences.edit { putLong(vodService.prefUpdate, currentTime) }
        for (i in 1..MAX_PAGINATION) {
            val videos = vodService.requestVodVideo(i)
            if (videos.isNullOrEmpty()) break
            val toUpdateVideos = videos.filter { it.vodTime > lastUpdate }
            videoDao.insertVideosFromVod(toUpdateVideos)
            if (toUpdateVideos.size < videos.size) break
        }
    }

    private fun getVodSource(source: String): VodSource {
        return when (source) {
            SYMBOL_BFZY -> BfzySource()
            SYMBOL_DYZY -> DyzySource()
            SYMBOL_FFZY -> FfzySource()
            SYMBOL_LZZY -> LzzySource()
            SYMBOL_WWZY -> WwzySource()
            SYMBOL_HDZY -> HdzySource()
            else -> throw IllegalArgumentException("Unknown source: $source")
        }
    }
}