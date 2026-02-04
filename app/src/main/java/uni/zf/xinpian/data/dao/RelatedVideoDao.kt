package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.RelatedVideo

@Dao
interface RelatedVideoDao {
    @Query("SELECT * FROM relatedvideo WHERE videoId = :videoId")
    fun getRelatedVideos(videoId: Int): Flow<List<RelatedVideo>>

    @Query("DELETE FROM relatedvideo WHERE videoId = :videoId")
    suspend fun deleteRelatedVideos(videoId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelatedVideos(relatedVideos: List<RelatedVideo>)

    @Transaction
    suspend fun updateRelatedVideos(videoId: Int, relatedVideos: List<RelatedVideo>) {
        deleteRelatedVideos(videoId)
        insertRelatedVideos(relatedVideos)
    }
}