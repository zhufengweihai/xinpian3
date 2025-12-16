package uni.zf.xinpian.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uni.zf.xinpian.data.dao.DownloadDao
import uni.zf.xinpian.data.dao.CategoryDao
import uni.zf.xinpian.data.dao.SearchHistoryDao
import uni.zf.xinpian.data.dao.SlideDataDao
import uni.zf.xinpian.data.dao.CustomTagDao
import uni.zf.xinpian.data.dao.TagDataDao
import uni.zf.xinpian.data.dao.WatchRecordDao
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.data.model.SearchHistory
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.data.dao.VideoDao
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.data.model.Vod
import uni.zf.xinpian.data.model.Category
import uni.zf.xinpian.data.model.CustomTag
import uni.zf.xinpian.data.model.DyTag
import uni.zf.xinpian.data.model.SlideData
import uni.zf.xinpian.data.model.VideoBrief

@Database(
    entities = [Video::class, Vod::class, WatchRecord::class, SearchHistory::class, DownloadVideo::class,
        DownloadItem::class, Category::class, SlideData::class, CustomTag::class, DyTag::class, VideoBrief::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchRecordDao(): WatchRecordDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun downloadDao(): DownloadDao

    abstract fun videoDao(): VideoDao

    abstract fun categoryDao(): CategoryDao

    abstract fun slideDataDao(): SlideDataDao

    abstract fun customTagDao(): CustomTagDao

    abstract fun tagDataDao(): TagDataDao
}