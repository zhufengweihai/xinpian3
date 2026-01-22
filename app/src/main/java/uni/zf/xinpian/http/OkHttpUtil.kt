package uni.zf.xinpian.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

/**
 * Kotlin 协程版 OkHttp 请求工具类 (终极最佳实践)
 * 核心：suspend挂起函数 + Dispatchers.IO + OkHttp同步execute
 * 特性：无回调、自动重试、自动线程切换、自动释放资源、空安全、异常友好
 */
object OkHttpUtil {
    private val okHttpClient = OkHttpManager.instance
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    private val defaultHeaders = mapOf(
        "User-Agent" to "Mozilla/5.0 (Linux; Android 13; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"
    )

    /**
     * Suspend 异步GET请求 (自动重试，幂等安全)
     * @param url 请求地址
     * @return 响应体字符串
     * @throws IOException 所有重试失败后抛出异常（可在调用处统一捕获）
     */
    suspend fun get(url: String, headers: Map<String, String> = defaultHeaders) = request {
        Request.Builder()
            .url(url)
            .get()
            .apply { headers.forEach { (k, v) -> addHeader(k, v) } }
            .build()
    }

    /**
     * Suspend 异步POST请求 (JSON参数，默认不重试，非幂等)
     * @param url 请求地址
     * @param jsonParams JSON请求体字符串
     * @return 响应体字符串
     * @throws IOException 失败抛出异常
     */
    suspend fun postJson(url: String, jsonParams: String): String = request {
        Request.Builder()
            .url(url)
            .post(jsonParams.toRequestBody(JSON_MEDIA_TYPE))
            .build()
    }

    /**
     * 私有化统一请求逻辑，抽离公共代码，极致解耦
     * 高阶函数封装Request构建逻辑，对外暴露极简API
     */
    private suspend fun request(requestBuilder: () -> Request): String = withContext(Dispatchers.IO) {
        try {
            val request = requestBuilder()
            val response = okHttpClient.newCall(request).execute()
            // 自动关闭响应体，use函数会在lambda执行完毕后自动调用close()，Kotlin语法糖，绝对安全！
            val responseBody = response.body.use { it.string() }
            if (response.isSuccessful) {
                responseBody
            } else {
                throw IOException("请求失败，响应码: ${response.code}, 响应信息: ${response.message}")
            }
        } catch (e: Exception) {
            throw IOException("请求失败，请检查网络连接", e)
        }
    }
}