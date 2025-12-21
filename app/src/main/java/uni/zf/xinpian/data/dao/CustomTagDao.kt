package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.objectbox.model.CustomTag

@Dao
interface CustomTagDao {
    @Query("SELECT * FROM customtag WHERE categoryId = :categoryId")
    fun getTagList(categoryId: String): Flow<List<CustomTag>>

    @Query("DELETE FROM customtag WHERE categoryId = :categoryId")
    suspend fun deleteTagList(categoryId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagList(customTagList: List<CustomTag>)

    @Transaction
    suspend fun updateTagList(categoryId: String, customTagList: List<CustomTag>) {
        deleteTagList(categoryId)
        insertTagList(customTagList)
    }
}