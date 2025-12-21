package uni.zf.xinpian

import androidx.room.Room.databaseBuilder
import io.dcloud.uniapp.UniApplication
import uni.zf.xinpian.data.AppDatabase
import uni.zf.xinpian.objectbox.ObjectBoxManager

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
        ObjectBoxManager.init(this)
    }

    companion object {
        lateinit var INSTANCE: App
    }
}