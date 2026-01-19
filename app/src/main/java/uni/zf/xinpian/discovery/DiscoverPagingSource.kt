package uni.zf.xinpian.discovery

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.discoverUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.DateMovieGroup

class DiscoverPagingSource() : PagingSource<Int, DateMovieGroup>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DateMovieGroup> {
        val pageNumber = params.key ?: 0
        try {
            val json = OkHttpUtil.get(discoverUrl.format(pageNumber + 1))
            if (json.isEmpty()) return LoadResult.Page(listOf(), null, null)
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val subArray = fullJsonObject["data"]?.jsonArray?.first()?.jsonObject?.get("sub")?.jsonArray
            val videos = subArray?.map { Json.decodeFromJsonElement<DateMovieGroup>(it) } ?: listOf()
            return LoadResult.Page(videos, null, pageNumber.plus(1))
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DateMovieGroup>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}