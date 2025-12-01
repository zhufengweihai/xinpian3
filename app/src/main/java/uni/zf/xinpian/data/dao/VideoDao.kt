package uni.zf.xinpian.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.data.model.VideoData
import uni.zf.xinpian.data.model.Vod
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.utils.mergeFromServer
import uni.zf.xinpian.utils.mergeFromVod

@Dao
interface VideoDao {

    @Transaction
    @Query("SELECT * FROM video WHERE id = :id")
    fun getVideoDataFlow(id: String): Flow<VideoData?>

    @Query("SELECT * FROM video WHERE id = :id")
    suspend fun getVideo(id: String): Video?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVods(vodList: List<Vod>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVod(vod: Vod)

    @RawQuery(observedEntities = [Video::class])
    fun getVideos(query: SupportSQLiteQuery): PagingSource<Int, Video>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchRecord(record: WatchRecord)

    @Transaction
    suspend fun insertVideosFromServer(videos: List<Video>) {
        videos.forEach {
            insertVideo(getVideo(it.id)?.mergeFromServer(it) ?: it)
        }
    }

    @Transaction
    suspend fun insertVideoFromVod(video: Video) {
        insertVideo(getVideo(video.id)?.mergeFromVod(video) ?: video)
        insertVods(video.vodList)
    }

    @Transaction
    suspend fun insertVideosFromVod(videos: List<Video>) {
        videos.forEach {
            insertVideo(getVideo(it.id)?.mergeFromVod(it) ?: it)
            insertVods(it.vodList)
        }
    }
}