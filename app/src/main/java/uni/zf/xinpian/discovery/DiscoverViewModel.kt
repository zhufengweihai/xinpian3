package uni.zf.xinpian.discovery

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
import uni.zf.xinpian.data.AppConst.discoverUrl
import uni.zf.xinpian.data.AppConst.rankOptionsUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.json.model.DateMovieGroup
import uni.zf.xinpian.json.model.Special
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.json.model.WeekRankOption

class DiscoverViewModel() : ViewModel() {
    val soonDataFlow: Flow<PagingData<DateMovieGroup>> = Pager(
        PagingConfig(
            20,
            5,
            true,
            20,
            100
        ), pagingSourceFactory = ::DiscoverPagingSource
    ).flow.cachedIn(viewModelScope)

    val specialDataFlow: Flow<PagingData<Special>> = Pager(
        PagingConfig(
            20,
            5,
            true,
            20,
            100
        ), pagingSourceFactory = ::SpecialPagingSource
    ).flow.cachedIn(viewModelScope)

    suspend fun getWeekRankOptions(): List<WeekRankOption> {
        val json = OkHttpUtil.get(rankOptionsUrl)
        if (json.isEmpty()) return listOf()
        val fullJsonObject = Json.parseToJsonElement(json).jsonObject
        val dataArray = fullJsonObject["data"]?.jsonArray
        return dataArray?.map { Json.decodeFromJsonElement<WeekRankOption>(it) } ?: listOf()
    }

    suspend fun getRankList(categoryId: Int): List<VideoData> {
        val url = "https://yebcdc.zxfmj.com/api/weekRank/list?category_id=$categoryId"
        val json = OkHttpUtil.get(url)
        if (json.isEmpty()) return listOf()
        val fullJsonObject = Json.parseToJsonElement(json).jsonObject
        val dataArray = fullJsonObject["data"]?.jsonArray
        return dataArray?.map { Json.decodeFromJsonElement<VideoData>(it) } ?: listOf()
    }
}