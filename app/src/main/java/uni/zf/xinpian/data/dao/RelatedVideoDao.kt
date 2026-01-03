package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.RelatedVideo

@Dao
interface RelatedVideoDao {
    @Query("SELECT * FROM relatedvideo WHERE videoId = :videoId")
    fun getRelatedVideos(videoId: Int): Flow<List<RelatedVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelatedVideos(relatedVideos: List<RelatedVideo>)
}