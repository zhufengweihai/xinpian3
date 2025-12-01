package uni.zf.xinpian.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadMedia
import uni.zf.xinpian.data.model.DownloadVideo

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideo(video: DownloadVideo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DownloadItem)

    suspend fun updateItems(items: List<DownloadItem>) {
        items.forEach { updateItem(it.url, it.state, it.bytesDownloaded, it.percentDownloaded) }
    }

    @Query(
        "UPDATE downloaditem SET state=:state,bytesDownloaded=:bytesDownloaded,percentDownloaded=:percentDownloaded WHERE " +
                "url=:url"
    )
    suspend fun updateItem(url: String, state: Int, bytesDownloaded: Long, percentDownloaded: Float)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: List<DownloadItem>)

    @Delete
    fun deleteVideo(video: DownloadVideo)

    @Query("DELETE FROM downloadvideo WHERE videoId = :id")
    suspend fun deleteVideo(id: String)

    @Query("DELETE FROM downloaditem WHERE url = :url")
    fun deleteItem(url: String)

    @Query(
        "SELECT downloadvideo.*,SUM(CASE WHEN downloaditem.state = 3 THEN 1 ELSE 0 END) AS downloaded," +
                "SUM(CASE WHEN downloaditem.state = 2 THEN 1 ELSE 0 END) AS downloading," +
                "SUM(bytesDownloaded) AS totalSize,COUNT(downloaditem.videoId) AS count " +
                "FROM downloadvideo LEFT JOIN downloaditem ON downloadvideo.videoId = downloaditem.videoId" +
                " GROUP BY downloadvideo.videoId"
    )
    fun getAllVideos(): Flow<List<DownloadVideo>>

    @Transaction
    @Query("SELECT * FROM downloadvideo")
    fun getAllMedias(): PagingSource<Int, DownloadMedia>

    @Query("SELECT * FROM downloaditem WHERE videoId = :id")
    fun getAllItems(id: String): Flow<List<DownloadItem>>

    @Query("SELECT url FROM downloaditem WHERE videoId = :id")
    suspend fun getItemsByVideoId(id: String): List<String>

    @Query("SELECT url FROM downloaditem")
    suspend fun getAllDownloadUrls(): List<String>

    @Query("SELECT url FROM downloaditem WHERE videoId = :id")
    fun getItemFlowByVideoId(id: String): Flow<List<String>>

    @Query("DELETE FROM downloadvideo")
    suspend fun deleteAllVideos()

    @Query("DELETE FROM downloaditem WHERE videoId = :id")
    suspend fun deleteAllItemsByVideoId(id: String)

    @Query("DELETE FROM downloaditem")
    fun deleteAllItems()

    @Transaction
    suspend fun deleteAll() {
        deleteAllItems()
        deleteAllVideos()
    }

    @Transaction
    suspend fun insertMedias(media: DownloadMedia) {
        media.video?.let { insertVideo(it) }
        media.items?.let { insertItems(it) }
    }

    @Transaction
    suspend fun upsertVideo(video: DownloadVideo, item: DownloadItem) {
        val id = insertVideo(video)
        if (id == -1L) {
            //incrementDownloadCount(video._id)
        }
        insertItem(item)
    }

    @Query("SELECT videoId FROM downloaditem WHERE url = :url")
    suspend fun getVideoIdByUrl(url: String): String?

    @Query("SELECT COUNT(*) FROM downloaditem WHERE videoId = :id")
    suspend fun getItemCountByVideoId(id: String): Int

    @Transaction
    suspend fun deleteItemThenDeleteVideo(url: String) {
        val videoId = getVideoIdByUrl(url)
        deleteItem(url)
        if (videoId != null) {
            getItemCountByVideoId(videoId).takeIf { it == 0 }?.let {
                deleteVideo(videoId)
            }
        }
    }

    @Query("SELECT EXISTS(SELECT * FROM downloaditem WHERE url = :url)")
    suspend fun isDownloadExists(url: String): Boolean
}