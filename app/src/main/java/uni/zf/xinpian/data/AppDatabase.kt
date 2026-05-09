package uni.zf.xinpian.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uni.zf.xinpian.data.dao.RelatedVideoDao
import uni.zf.xinpian.data.dao.SearchHistoryDao
import uni.zf.xinpian.data.dao.WatchHistoryDao
import uni.zf.xinpian.data.model.RelatedVideo
import uni.zf.xinpian.data.model.SearchHistory
import uni.zf.xinpian.data.model.WatchHistory

@Database(
    entities = [SearchHistory::class, RelatedVideo::class, WatchHistory::class],
    version = 3,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun relatedVideoDao(): RelatedVideoDao

    abstract fun watchHistoryDao(): WatchHistoryDao
}
