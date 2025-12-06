package uni.zf.xinpian

import androidx.room.Room.databaseBuilder
import io.dcloud.uniapp.UniApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uni.zf.xinpian.data.AppDatabase

class App : UniApplication() {
    lateinit var appDb: AppDatabase
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appDb = databaseBuilder(applicationContext, AppDatabase::class.java, "xinpian").build()
    }

    private val _imgDomains = MutableStateFlow<List<String>>(emptyList())
    val imgDomains: StateFlow<List<String>> = _imgDomains.asStateFlow()

    fun setImgDomains(imgDomains: List<String>) {
        _imgDomains.value = imgDomains
    }

    companion object {
        lateinit var INSTANCE: App
    }
}