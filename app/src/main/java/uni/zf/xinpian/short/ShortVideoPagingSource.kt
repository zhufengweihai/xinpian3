package uni.zf.xinpian.short

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.shortUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.ShortVideo
import uni.zf.xinpian.utils.createHeaders

class ShortVideoPagingSource(private val secret: String, private val userAgent: String) : PagingSource<Int, ShortVideo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShortVideo> {
        val pageNumber = params.key ?: 0
        try {
            val json = OkHttpUtil.get(shortUrl.format(pageNumber + 1), createHeaders(secret, userAgent))
            if (json.isEmpty()) return LoadResult.Page(listOf(), null, null)
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val videos = dataArray?.map { Json.decodeFromJsonElement<ShortVideo>(it) } ?: listOf()
            return LoadResult.Page(videos, null, pageNumber.plus(1))
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ShortVideo>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}