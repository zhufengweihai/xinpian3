package uni.zf.xinpian.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .build()

private val defaultHeaders = mapOf(
    "User-Agent" to "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0"
)

suspend fun requestUrl(url: String, customHeaders: Map<String, String>? = null): String {
    return request(url, customHeaders ?: defaultHeaders)
}

suspend fun requestUrls(urls: List<String>, customHeaders: Pair<String, String>? = null): String {
    return repeatRequest(urls, if (customHeaders == null) defaultHeaders else defaultHeaders + customHeaders)
}

private suspend fun repeatRequest(urls: List<String>, headers: Map<String, String>): String {
    for (url in urls) {
        val result = request(url, headers)
        if (result.isNotBlank()) return result
    }
    return ""
}

private suspend fun request(url: String, headers: Map<String, String>): String {
    for (i in 0 until 3) {
        val requestBuilder = Request.Builder()
            .url(url)
            .apply { headers.forEach { (k, v) -> addHeader(k, v) } }

        val response = executeRequest(requestBuilder.build())
        if (response?.code == 200) {
            response.body.string().let {
                if (it.isNotBlank() && !it.contains("异常请求")) return it
            }
        }
        delay(1000)
    }
    return ""
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun executeRequest(request: Request): Response? = coroutineScope {
    suspendCancellableCoroutine {
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                it.resume(response) { _, _, _ -> }
            }

            override fun onFailure(call: Call, e: IOException) {
                it.resume(null) { _, _, _ -> }
            }
        })
    }
}