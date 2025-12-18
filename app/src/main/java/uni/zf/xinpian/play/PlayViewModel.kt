package uni.zf.xinpian.play

import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.data.model.VideoData
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.download.DownloadTracker

class PlayViewModel : ViewModel() {
    private val watchRecordDao by lazy { App.INSTANCE.appDb.watchRecordDao() }
    private val downloadDao by lazy { App.INSTANCE.appDb.downloadDao() }
    private val videoDao by lazy { App.INSTANCE.appDb.videoDao() }

    fun getVideoDataFlow(id: String): Flow<VideoData?> {
        return videoDao.getVideoDataFlow(id)
    }

    suspend fun saveWatchRecord(watchRecord: WatchRecord) {
        watchRecordDao.insertOrUpdate(watchRecord)
    }

    suspend fun saveDownloadVideo(video: DownloadVideo) {
        downloadDao.insertVideo(video)
    }

    suspend fun saveDownloadItem(item: DownloadItem) {
        downloadDao.insertItem(item)
    }

    fun getAllItemsByVideoId(_id: String): Flow<List<String>> {
        return downloadDao.getItemFlowByVideoId(_id)
    }

    @OptIn(UnstableApi::class)
    fun pauseLastDownload(uri: Uri) {
        viewModelScope.launch {
            if (!isDownloadExists(uri.toString())) DownloadTracker.pauseDownload(uri)
        }
    }

    private suspend fun isDownloadExists(url: String) = downloadDao.isDownloadExists(url)
}