package uni.zf.xinpian.http

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import uni.zf.xinpian.App
import java.io.IOException
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

/**
 * Kotlin 协程版 OkHttp 请求工具类 (终极最佳实践)
 * 核心：suspend挂起函数 + Dispatchers.IO + OkHttp同步execute
 * 特性：无回调、自动重试、自动线程切换、自动释放资源、空安全、异常友好
 * 新增：域名故障自动切换 - 请求失败时自动切换到下一个可用域名重试
 */
object OkHttpUtil {
    private const val TAG = "OkHttpUtil"
    private val okHttpClient = OkHttpManager.instance
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    private val defaultHeaders = mapOf(
        "User-Agent" to "Mozilla/5.0 (Linux; Android 13; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"
    )

    /**
     * Suspend 异步GET请求 (支持域名故障切换)
     * @param url 请求地址
     * @param headers 请求头
     * @return 响应体字符串
     * @throws IOException 所有域名都失败后抛出异常
     */
    suspend fun get(url: String, headers: Map<String, String> = defaultHeaders): String {
        return requestWithFailover(url, headers) { actualUrl, actualHeaders ->
            Request.Builder()
                .url(actualUrl)
                .get()
                .apply { actualHeaders.forEach { (k, v) -> addHeader(k, v) } }
                .build()
        }
    }

    /**
     * Suspend 异步POST请求 (JSON参数，支持域名故障切换)
     * @param url 请求地址
     * @param jsonParams JSON请求体字符串
     * @return 响应体字符串
     * @throws IOException 失败抛出异常
     */
    suspend fun postJson(url: String, jsonParams: String): String {
        return requestWithFailover(url, defaultHeaders) { actualUrl, actualHeaders ->
            Request.Builder()
                .url(actualUrl)
                .post(jsonParams.toRequestBody(JSON_MEDIA_TYPE))
                .apply { actualHeaders.forEach { (k, v) -> addHeader(k, v) } }
                .build()
        }
    }

    /**
     * 带域名故障切换的请求逻辑
     * 如果当前域名请求失败，自动切换到下一个域名并替换URL中的域名后重试
     *
     * @param originalUrl 原始请求URL
     * @param originalHeaders 原始请求头
     * @param buildRequest 根据实际URL和Headers构建Request的lambda
     */
    private suspend fun requestWithFailover(
        originalUrl: String,
        originalHeaders: Map<String, String>,
        buildRequest: (String, Map<String, String>) -> Request
    ): String = withContext(Dispatchers.IO) {
        // 如果不是动态域名URL，直接请求不做故障切换
        if (!isDomainUrl(originalUrl)) {
            return@withContext executeRequest(buildRequest(originalUrl, originalHeaders))
        }

        val context = App.INSTANCE.applicationContext
        val triedDomains = mutableSetOf<String>()
        var lastException: Exception? = null

        // 尝试所有可用域名
        repeat(10) {
            val currentDomain = DomainManager.currentDomain
            if (currentDomain in triedDomains) {
                // 所有域名都已尝试过，退出循环
                throw lastException ?: IOException("所有域名请求均失败")
            }
            triedDomains.add(currentDomain)

            // 将URL中的域名替换为当前活跃域名
            val actualUrl = DomainManager.replaceUrlDomain(originalUrl)
            // 同步更新 Host 请求头
            val actualHeaders = updateHostHeader(originalHeaders, actualUrl)

            try {
                val result = executeRequest(buildRequest(actualUrl, actualHeaders))
                if (result.isNotEmpty()) {
                    return@withContext result
                }
                // 空响应也视为失败，尝试下一个域名
                Log.w(TAG, "域名 $currentDomain 返回空响应")
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "域名 $currentDomain 请求失败: ${e.message}")
            }

            // 切换到下一个域名
            val switched = DomainManager.switchToNextDomain(context)
            if (!switched) {
                throw lastException ?: IOException("没有更多可用域名")
            }
            Log.d(TAG, "切换到域名: ${DomainManager.currentDomain}")
        }

        throw lastException ?: IOException("所有域名请求均失败")
    }

    /**
     * 更新请求头中的 Host 字段为实际请求URL的host
     */
    private fun updateHostHeader(headers: Map<String, String>, url: String): Map<String, String> {
        if (!headers.containsKey("Host")) return headers
        val newHost = url.substringAfter("://").substringBefore("/")
        return headers.toMutableMap().apply { put("Host", newHost) }
    }

    /**
     * 执行单次请求
     */
    private fun executeRequest(request: Request): String {
        var response: Response? = null
        try {
            response = okHttpClient.newCall(request).execute()
            val text = if (response.isSuccessful) response.body.string() else ""
            return text
        } catch (e: Exception) {
            throw IOException("请求异常: ${e.message}", e)
        } finally {
            response?.close()
        }
    }

    /**
     * 判断URL是否使用了动态域名（需要域名切换支持）
     * 排除第三方URL（如 github.com 等）
     */
    private fun isDomainUrl(url: String): Boolean {
        val currentDomain = DomainManager.currentDomain
        return url.contains(currentDomain) ||
                url.contains("zxfmj.com") ||
                url.contains("970xw.com")
    }
}
