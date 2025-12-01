package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.WatchRecord

@Dao
interface WatchRecordDao {
    @Query("SELECT * FROM WatchRecord ORDER BY lastWatchTime DESC")
    fun getAll(): Flow<List<WatchRecord>>

    @Query("SELECT * FROM WatchRecord WHERE videoId = :id")
    suspend fun getWatchRecord(id: String): WatchRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: WatchRecord)

    @Delete
    suspend fun deleteWatchRecord(record: WatchRecord)

    @Query("DELETE FROM WatchRecord")
    suspend fun deleteAll()
}
