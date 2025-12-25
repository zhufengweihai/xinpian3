package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.json.model.VideoData

@Dao
interface VideoDataDao {
    @Query("SELECT * FROM videodata WHERE id = :id")
    fun getVideoDataFlow(id: Int): Flow<VideoData?>
}