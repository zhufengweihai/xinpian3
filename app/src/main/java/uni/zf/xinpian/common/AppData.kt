package uni.zf.xinpian.common

import android.content.Context
import android.webkit.WebSettings
import androidx.startup.Initializer
import com.alibaba.fastjson.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.utils.requestUrl

class AppData private constructor(context: Context) {
    private val IMAGE_DOMAIN_URL = "https://qqhx9o.zxbwv.com/api/resourceDomainConfig"
    private val FENLEI_URL = "https://qqhx9o.zxbwv.com/api/term/home_fenlei"
    val userAgent: String = WebSettings.getDefaultUserAgent(context)
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
        val customHeaders = mapOf(
            "Host" to "qqhx9o.zxbwv.com",
            "Connection" to "keep-alive",
            "sec-ch-ua-platform" to "Android",
            "timestamp" to (System.currentTimeMillis() / 1000).toString(),
            "signature" to "7228a5a21f1010fac86f1219216c637a",
            "sec-ch-ua-mobile" to "\"Chromium\";v=\"142\", \"Android WebView\";v=\"142\", \"Not_A Brand\";v=\"99\"",
            "sec-ch-ua-mobile" to "?1",
            "User-Agent" to userAgent,
            "Accept" to "application/json, text/plain, */*",
            "version" to "417",
            "X-Requested-With" to "com.qihoo.jp22",
            "Sec-Fetch-Site" to "cross-site",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Dest" to "empty",
            "Accept-Encoding" to "gzip, deflate, br, zstd",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,it-IT;q=0.6,it;q=0.5"
        )
        val dataString = requestUrl(FENLEI_URL, customHeaders)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseFenlei(it as Map<String, Any>) }
    }

    suspend fun requestImgDomains(): List<String> {
        val customHeaders = mapOf(
            "Host" to "qqhx9o.zxbwv.com",
            "Connection" to "keep-alive",
            "sec-ch-ua-platform" to "Android",
            "timestamp" to (System.currentTimeMillis() / 1000).toString(),
            "signature" to "7228a5a21f1010fac86f1219216c637a",
            "sec-ch-ua-mobile" to "\"Chromium\";v=\"142\", \"Android WebView\";v=\"142\", \"Not_A Brand\";v=\"99\"",
            "sec-ch-ua-mobile" to "?1",
            "User-Agent" to userAgent,
            "Accept" to "application/json, text/plain, */*",
            "version" to "417",
            "X-Requested-With" to "com.qihoo.jp22",
            "Sec-Fetch-Site" to "cross-site",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Dest" to "empty",
            "Accept-Encoding" to "gzip, deflate, br, zstd",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,it-IT;q=0.6,it;q=0.5"
        )
        val dataString = requestUrl(IMAGE_DOMAIN_URL, customHeaders)
        val imgDomainString = JSON.parseObject(dataString).getJSONObject("data").getString("imgDomain")
        return imgDomainString.split(",").map { "https://$it" }
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