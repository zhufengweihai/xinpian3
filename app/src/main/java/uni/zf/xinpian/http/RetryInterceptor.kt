package uni.zf.xinpian.http

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(
    private val maxRetryCount: Int,
    private val retryDelayBase: Long
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var lastException: IOException? = null
        var response: Response? = null

        for (retryNum in 0..maxRetryCount) {
            try {
                response = chain.proceed(originalRequest)
                if (response.isSuccessful) {
                    return response
                }
            } catch (e: IOException) {
                lastException = e
            }

            if (retryNum >= maxRetryCount || !isRequestRetryable(originalRequest) || !isFailureRetryable(response, lastException)) {
                break
            }

            val delayTime = retryDelayBase * (1 shl retryNum)
            Thread.sleep(delayTime)
        }

        lastException?.let { throw it }
        return response ?: throw IOException("请求无响应且无异常信息")
    }

    private fun isRequestRetryable(request: Request): Boolean {
        return request.method.uppercase() in listOf("GET", "HEAD")
    }

    private fun isFailureRetryable(response: Response?, exception: IOException?): Boolean {
        if (exception != null) return true
        return response?.code?.let { code -> code >= 500 || code == 429 } ?: false
    }
}