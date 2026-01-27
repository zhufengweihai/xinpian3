package uni.zf.xinpian.play

import android.app.Application
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup
import uni.zf.xinpian.App
import uni.zf.xinpian.data.AppConst.ARG_VIDEO_ID
import uni.zf.xinpian.data.AppConst.recoUrl
import uni.zf.xinpian.data.AppConst.videoUrl
import uni.zf.xinpian.download.DownloadTracker
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.createVideoDataStore
import uni.zf.xinpian.data.model.RelatedVideo
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.utils.createHeaders
import java.net.URL

class PlayViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val videoId = savedStateHandle.get<Int>(ARG_VIDEO_ID) ?: 0
    private val app = getApplication<Application>()
    private val videoDataStore = app.createVideoDataStore(videoId)
    private val relatedVideoDao by lazy { App.INSTANCE.appDb.relatedVideoDao() }
    private val downloadDao by lazy { App.INSTANCE.appDb.downloadDao() }

    private val jsonConfig = Json {
        isLenient = true  // 允许非严格格式的 JSON
        ignoreUnknownKeys = true  // 忽略未知键
    }

    fun getVideoData() = videoDataStore.data

    fun getRelatedVideos(): Flow<List<RelatedVideo>> = relatedVideoDao.getRelatedVideos(videoId)

    fun requestVideoData() {
        viewModelScope.launch {
            try {
                val json = OkHttpUtil.get(videoUrl.format(videoId), createHeaders(app))
                if (json.isEmpty()) return@launch
                val jsonObject = Json.parseToJsonElement(json).jsonObject
                jsonObject["data"]?.jsonObject?.let {
                    val videoData = jsonConfig.decodeFromJsonElement<VideoData>(it)
                    videoDataStore.updateData { videoData }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestRecommend() {
        viewModelScope.launch {
            try {
                val json = OkHttpUtil.get(recoUrl.format(videoId), createHeaders(app))
                if (json.isEmpty()) return@launch
                val jsonArray = Json.parseToJsonElement(json).jsonObject["data"]?.jsonArray
                val relatedVideos = jsonArray?.map {
                    RelatedVideo(
                        videoId,
                        it.jsonObject["title"]?.jsonPrimitive?.content ?: "",
                        it.jsonObject["path"]?.jsonPrimitive?.content ?: "",
                        it.jsonObject["id"]?.jsonPrimitive?.int ?: 0,
                        it.jsonObject["score"]?.jsonPrimitive?.content ?: ""
                    )
                } ?: emptyList()
                relatedVideoDao.insertRelatedVideos(relatedVideos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestDoubanRecommend(doubanId: Int) {
        viewModelScope.launch {
            try {
                val relatedVideos = withContext(Dispatchers.IO) {
                    parseRelatedVideos("https://m.douban.com/movie/subject/$doubanId")
                }
                relatedVideoDao.insertRelatedVideos(relatedVideos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun parseRelatedVideos(url: String): List<RelatedVideo> {
        val document = Jsoup.parse(URL(url), 3000)
        val recElements = document.select("div.recommendations-bd")
        if (recElements.isEmpty()) return emptyList()
        val dtElements = recElements.select("dt")
        val relatedMovies = mutableListOf<RelatedVideo>()
        for (dtElement in dtElements) {
            val href = dtElement.select("a").attr("href")
            val doubanId = extractMovieIdFromUrl(href)
            val imgElements = dtElement.select("img")
            val imageUrl = imgElements.attr("src")
            val title = imgElements.attr("alt")
            if (title.isNotEmpty() && imageUrl.isNotEmpty() && doubanId.isNotEmpty()) {
                relatedMovies.add(RelatedVideo(videoId, title, imageUrl, doubanId.toInt()))
            }
        }
        return relatedMovies
    }

    fun extractMovieIdFromUrl(url: String): String {
        val idPattern = Regex("/subject/(\\d+)(?:/|\\?|\\s|$)")
        val matchResult = idPattern.find(url)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    @OptIn(UnstableApi::class)
    fun pauseLastDownload(uri: Uri) {
        viewModelScope.launch {
            if (!isDownloadExists(uri.toString())) DownloadTracker.pauseDownload(uri)
        }
    }

    private suspend fun isDownloadExists(url: String) = downloadDao.isDownloadExists(url)
}