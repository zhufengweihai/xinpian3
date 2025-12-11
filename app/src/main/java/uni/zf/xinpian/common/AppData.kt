package uni.zf.xinpian.common

import android.content.Context
import android.webkit.WebSettings
import androidx.startup.Initializer
import com.alibaba.fastjson.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.fenleiUrl
import uni.zf.xinpian.utils.generateSignature
import uni.zf.xinpian.utils.imgDomainUrl
import uni.zf.xinpian.utils.initUrl
import uni.zf.xinpian.utils.requestUrl

class AppData private constructor(context: Context) {
    val userAgent: String = WebSettings.getDefaultUserAgent(context)
    val secret: String
    val imgDomains: List<String>
    val fenleiList: List<Fenlei>

    init {
        val result = runBlocking(Dispatchers.IO) {
            val secret = requestSecret()
            val imgDomains = requestImgDomains(secret)
            val fenleiList = requestFenlei(secret)
            Triple(secret, imgDomains, fenleiList)
        }
        secret = result.first
        imgDomains = result.second
        fenleiList = result.third
    }

    suspend fun requestSecret(): String {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp)
        val dataString = requestUrl(initUrl, createHeaders(timestamp, signature, userAgent = userAgent))
        return JSON.parseObject(dataString).getJSONObject("data").getString("secret")
    }

    suspend fun requestImgDomains(secret: String): List<String> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp, secret)
        val dataString = requestUrl(imgDomainUrl, createHeaders(timestamp, signature, userAgent))
        val imgDomainString = JSON.parseObject(dataString).getJSONObject("data").getString("imgDomain")
        return imgDomainString.split(",").map { "https://$it" }
    }

    suspend fun requestFenlei(secret: String): List<Fenlei> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp, secret)
        val dataString = requestUrl(fenleiUrl, createHeaders(timestamp, signature, userAgent))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseFenlei(it as Map<String, Any>) }
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