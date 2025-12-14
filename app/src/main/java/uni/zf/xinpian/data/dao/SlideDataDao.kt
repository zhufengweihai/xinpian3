package uni.zf.xinpian.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.SlideData

interface SlideDataDao {
    @Query("SELECT * FROM slidedata WHERE categoryId = :categoryId")
    fun getSlideDataList(categoryId: String): Flow<List<SlideData>>

    @Query("DELETE FROM slidedata WHERE categoryId = :categoryId")
    suspend fun deleteSlideDataList(categoryId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlideDataList(slideDataList: List<SlideData>)

    @Transaction
    suspend fun updateSlideDataList(categoryId: String, slideDataList: List<SlideData>) {
        deleteSlideDataList(categoryId)
        insertSlideDataList(slideDataList)
    }
}