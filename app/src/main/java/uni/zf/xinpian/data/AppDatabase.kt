package uni.zf.xinpian.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uni.zf.xinpian.data.dao.DownloadDao
import uni.zf.xinpian.data.dao.SearchHistoryDao
import uni.zf.xinpian.data.dao.WatchRecordDao
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.data.model.SearchHistory
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.data.dao.VideoDao
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.data.model.Vod

@Database(
    entities = [Video::class, Vod::class, WatchRecord::class, SearchHistory::class, DownloadVideo::class,
        DownloadItem::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchRecordDao(): WatchRecordDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun downloadDao(): DownloadDao

    abstract fun videoDao(): VideoDao
}