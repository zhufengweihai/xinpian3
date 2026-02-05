package uni.zf.xinpian

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.room.Room.databaseBuilder
import io.dcloud.uniapp.UniApplication
import uni.zf.xinpian.data.AppDatabase
import uni.zf.xinpian.json.CategorySerializer
import uni.zf.xinpian.json.CustomTagSerializer
import uni.zf.xinpian.json.DyTagSerializer
import uni.zf.xinpian.json.SlideListSerializer
import uni.zf.xinpian.json.VideoSerializer
import uni.zf.xinpian.json.model.CategoryList
import uni.zf.xinpian.json.model.CustomTags
import uni.zf.xinpian.json.model.DyTagList
import uni.zf.xinpian.json.model.SlideList
import uni.zf.xinpian.json.model.VideoData
import java.io.File

class App : UniApplication() {
    lateinit var appDb: AppDatabase
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appDb =
            databaseBuilder(applicationContext, AppDatabase::class.java, "xinpian")
                .fallbackToDestructiveMigrationFrom(
                    true,
                    1
                )
                .build()
    }

    companion object {
        lateinit var INSTANCE: App
    }

    private val videoDataStoreMap = mutableMapOf<Int, DataStore<VideoData?>>()

    fun getVideoDataStore(videoId: Int): DataStore<VideoData?> {
        return videoDataStoreMap.getOrPut(videoId) {
            DataStoreFactory.create(
                VideoSerializer(),
                produceFile = { File(filesDir, "video_$videoId.json") }
            )
        }
    }

    val categoryDataStore: DataStore<CategoryList> by dataStore(
        fileName = "category.json",
        serializer = CategorySerializer(),
    )

    private val customTagDataStoreMap = mutableMapOf<Int, DataStore<CustomTags>>()

    fun getCustomTagDataStore(categoryId: Int): DataStore<CustomTags> {
        return customTagDataStoreMap.getOrPut(categoryId) {
            DataStoreFactory.create(
                CustomTagSerializer(),
                produceFile = { File(filesDir, "custom_tag_$categoryId.json") }
            )
        }
    }

    private val dyTagDataStoreMap = mutableMapOf<Int, DataStore<DyTagList>>()

    fun getDyTagDataStore(categoryId: Int): DataStore<DyTagList> {
        return dyTagDataStoreMap.getOrPut(categoryId) {
            DataStoreFactory.create(
                DyTagSerializer(),
                produceFile = { File(filesDir, "dy_tag_$categoryId.json") }
            )
        }
    }

    private val slideDataStoreMap = mutableMapOf<Int, DataStore<SlideList>>()

    fun getSlideDataStore(categoryId: Int): DataStore<SlideList> {
        return slideDataStoreMap.getOrPut(categoryId) {
            DataStoreFactory.create(
                SlideListSerializer(),
                produceFile = { File(filesDir, "slide_list_$categoryId.json") }
            )
        }
    }
}