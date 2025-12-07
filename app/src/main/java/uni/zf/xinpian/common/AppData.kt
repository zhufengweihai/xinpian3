package uni.zf.xinpian.common

import android.content.Context
import androidx.startup.Initializer
import com.alibaba.fastjson.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.utils.requestUrl

class AppData private constructor(context: Context) {
    private val IMAGE_DOMAIN_URL = "https://qqhx9o.zxbwv.com/api/resourceDomainConfig"
    private val FENLEI_URL = "https://qqhx9o.zxbwv.com/api/term/home_fenlei"
    val imgDomains: List<String>
    val fenleiList: List<Fenlei>

    init {
        imgDomains = runBlocking(Dispatchers.IO) {
            requestImgDomains()
        }
        fenleiList = runBlocking(Dispatchers.IO) {
            requestFenlei()
        }
    }


    suspend fun requestFenlei(): List<Fenlei> {
        val dataString = requestUrl(FENLEI_URL)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseFenlei(it as Map<String, Any>) }
    }

    suspend fun requestImgDomains(): List<String> {
        val dataString = requestUrl(IMAGE_DOMAIN_URL)
        val imgDomainString = JSON.parseObject(dataString).getJSONObject("data").getString("imgDomain")
        return imgDomainString.split(",")
    }

    private fun parseFenlei(data: Map<String, Any>): Fenlei {
        return Fenlei(data["id"].toString(), data["name"].toString(), data["title"]?.toString())
    }

    companion object {
        @Volatile
        private var INSTANCE: AppData? = null

        fun getInstance(context: Context): AppData {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppData(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

class AppDataInitializer : Initializer<AppData> {
    override fun create(context: Context): AppData {
        return AppData.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}