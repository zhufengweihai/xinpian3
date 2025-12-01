package uni.zf.xinpian.service

import android.app.job.JobParameters
import android.app.job.JobService
import androidx.core.content.edit
import io.dcloud.unicloud.uniCloud
import io.dcloud.uts.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uni.zf.xinpian.App

private const val PREF_LAST_SYNC = "last_sync_time"
private const val DEFAULT_LAST_SYNC = 1745071200000
private const val SYNC_INTERVAL = 30 * 60 * 1000L // 30 minutes
private const val FIELD = "vod_id,name,name_en,status,vod_time,image,genres,actors,directors,areas,year,db_id" +
        ",db_score,category,hotness"

class VideoSyncService : JobService() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val preferences by lazy { getSharedPreferences("xinpian", MODE_PRIVATE) }
    private val videoDao by lazy { App.INSTANCE.appDb.videoDao() }

    override fun onStartJob(params: JobParameters?): Boolean {
        serviceScope.launch {
            val currentTime = System.currentTimeMillis()
            val lastSyncTime = preferences.getLong(PREF_LAST_SYNC, DEFAULT_LAST_SYNC)
            if (lastSyncTime - currentTime > SYNC_INTERVAL) {
                syncAllVod(lastSyncTime)
                preferences.edit { putLong(PREF_LAST_SYNC, currentTime) }
            }
        }
        return true
    }

    private suspend fun syncAllVod(lastSyncTime: Long) {
        val collection = uniCloud.databaseForJQL().collection("video")
        val dbQuery = collection.where("vod_time>$lastSyncTime").field(FIELD)
        val num = 100 * 0
        val result = await(dbQuery.skip(num).limit(100).get())
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }
}