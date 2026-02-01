package uni.zf.xinpian.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.utils.createHeaders

class VideoPagingSource(private val url: String, val headers: Map<String, String>) : PagingSource<Int, TagData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TagData> {
        val pageNumber = params.key ?: 0
        try {
            val json = OkHttpUtil.get(url.format(pageNumber + 1), headers)
            if (json.isEmpty()) return LoadResult.Page(listOf(), null, null)
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val videos = dataArray?.map { Json.decodeFromJsonElement<TagData>(it) } ?: listOf()
            val nextKey = if (videos.size < params.loadSize) null else pageNumber.plus(1)
            return LoadResult.Page(videos, null, nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TagData>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}