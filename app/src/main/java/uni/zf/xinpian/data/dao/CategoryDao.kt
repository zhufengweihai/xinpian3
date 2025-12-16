package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Query("DELETE FROM category")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryList: List<Category>)

    @Transaction
    suspend fun updateAll(categoryList:List<Category>) {
        deleteAll()
        insertAll(categoryList)
    }
}