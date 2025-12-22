package uni.zf.xinpian.play

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.objectbox.kotlin.equal
import io.objectbox.kotlin.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uni.zf.xinpian.App
import uni.zf.xinpian.data.AppConst.videoUrl
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.data.model.video.VideoData
import uni.zf.xinpian.download.DownloadTracker
import uni.zf.xinpian.objectbox.ObjectBoxManager
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class PlayViewModel : ViewModel() {
    private val watchRecordDao by lazy { App.INSTANCE.appDb.watchRecordDao() }
    private val downloadDao by lazy { App.INSTANCE.appDb.downloadDao() }

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    fun getVideoData(videoId: Int): Flow<MutableList<VideoData?>> {
        val videoBox = ObjectBoxManager.getBox(VideoData::class.java)
        return videoBox.query().equal(VideoData_.id, videoId).build().flow()
    }

    suspend fun requestVideoData(id: Int, context: Context) {
        val json = requestUrl(videoUrl.format(id), createHeaders(context))
        val gson = Gson()
        val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
        val targetDataObj = fullJsonObject.getAsJsonObject("data")
        val videoData = gson.fromJson(targetDataObj, VideoData::class.java)
        val videoBox = ObjectBoxManager.getBox(VideoData::class.java)
        videoBox.put(videoData)
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