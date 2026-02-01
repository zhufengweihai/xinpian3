package uni.zf.xinpian.discovery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.rankOptionsUrl
import uni.zf.xinpian.data.AppConst.weekRankUrl
import uni.zf.xinpian.discovery.DiscoverPagingSource
import uni.zf.xinpian.discovery.SpecialPagingSource
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.DateMovieGroup
import uni.zf.xinpian.json.model.RankingItem
import uni.zf.xinpian.json.model.Special
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.json.model.WeekRankOption

class RankViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val categoryId = ssh.get<Int>(ARG_CATEGORY) ?: 0

    suspend fun getWeekRankOptions(): List<WeekRankOption> {
        try {
            val json = OkHttpUtil.get(rankOptionsUrl)
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            return dataArray?.map { Json.decodeFromJsonElement<WeekRankOption>(it) } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }

    suspend fun getWeekRankList(): List<RankingItem> {
        try {
            val url = weekRankUrl.format(categoryId)
            val json = OkHttpUtil.get(url)
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            return dataArray?.map { Json.decodeFromJsonElement<RankingItem>(it) } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }
}