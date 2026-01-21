package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.WatchHistory
import uni.zf.xinpian.data.model.WatchRecord

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM WatchHistory ORDER BY lastWatchTime DESC")
    fun getAll(): Flow<List<WatchHistory>>

    @Query("SELECT * FROM WatchHistory WHERE videoId = :id")
    suspend fun getWatchHistory(id: Int): WatchHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: WatchHistory)

    @Delete
    suspend fun deleteWatchHistory(record: WatchHistory)

    @Query("DELETE FROM WatchHistory")
    suspend fun deleteAll()
}
