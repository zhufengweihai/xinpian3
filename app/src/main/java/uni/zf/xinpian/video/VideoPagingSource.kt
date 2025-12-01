package uni.zf.xinpian.video

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.dcloud.unicloud.uniCloud
import io.dcloud.uts.await
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.Video

class VideoPagingSource(private val query: String, private val orderBy: String, private val pageSize: Int) :
    PagingSource<Int, Video>() {
    private val videoDao by lazy { App.INSTANCE.appDb.videoDao() }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        val pageNumber = params.key ?: 0
        val collection = uniCloud.databaseForJQL().collection("video2")
        try {
            val num = pageSize * pageNumber
            val result = await(
                if (query.isEmpty()) {
                    collection.field(FIELD).orderBy(orderBy).skip(num).limit(pageSize).get()
                } else {
                    collection.where(query).field(FIELD).orderBy(orderBy).skip(num).limit(pageSize).get()
                }
            )
            val videos = Video.from(result.data)
            videoDao.insertVideosFromServer(videos)
            return LoadResult.Page(
                data = videos,
                prevKey = null,
                nextKey = if (result.data.isEmpty()) null else pageNumber.plus(1)
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val FIELD = "name,vod_ids,category,status,image,db_id,db_score,vod_time"
    }
}