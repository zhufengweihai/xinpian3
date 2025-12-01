package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.SearchHistory

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory)

    @Query("DELETE FROM searchhistory WHERE keyword = :keyword")
    suspend fun delete(keyword: String?)

    @Query("DELETE FROM searchhistory")
    suspend fun deleteAll()

    @Query("SELECT * FROM searchhistory ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SearchHistory>>

    @Query("DELETE FROM searchhistory WHERE keyword NOT IN (SELECT keyword FROM searchhistory ORDER BY timestamp DESC LIMIT 8)")
    suspend fun deleteOldest()
}