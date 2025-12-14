package uni.zf.xinpian.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.CustomTag

interface CustomTagDao {
    @Query("SELECT * FROM customtag WHERE categoryId = :categoryId")
    fun getTagList(categoryId: String): Flow<List<CustomTag>>

    @Query("DELETE FROM customtag WHERE categoryId = :fenleiId")
    suspend fun deleteTagList(fenleiId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagList(slideDataList: List<CustomTag>)

    @Transaction
    suspend fun updateTagList(fenleiId: String, customTagList: List<CustomTag>) {
        deleteTagList(fenleiId)
        insertTagList(customTagList)
    }
}