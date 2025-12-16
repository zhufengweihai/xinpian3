package uni.zf.xinpian.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.DyTag
import uni.zf.xinpian.data.model.TagData
import uni.zf.xinpian.data.model.VideoBrief

@Dao
interface TagDataDao {
    @Transaction
    @Query("SELECT * FROM dytag WHERE categoryId = :categoryId")
    fun getTagDatas(categoryId: String): Flow<List<TagData>>

    @Query("DELETE FROM dytag WHERE categoryId = :categoryId")
    suspend fun deleteTagDatas(categoryId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDyTag(dyTag: DyTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoBriefs(videoBriefs: List<VideoBrief>)

    @Transaction
    suspend fun updateTagDatas(categoryId: String, tagDataList: List<TagData>) {
        deleteTagDatas(categoryId)
        tagDataList.forEach { tagData ->
            insertDyTag(tagData.dyTag)
            insertVideoBriefs(tagData.videoList)
        }
    }
}