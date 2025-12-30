package uni.zf.xinpian.http

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpManager {
    private const val CONNECT_TIMEOUT = 10L
    private const val READ_TIMEOUT = 20L
    private const val WRITE_TIMEOUT = 20L
    private const val MAX_RETRY_COUNT = 3
    private const val RETRY_DELAY_BASE = 1000L

    val instance: OkHttpClient by lazy {
        val interceptor = RetryInterceptor(MAX_RETRY_COUNT, RETRY_DELAY_BASE)
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false) // 关闭内置重试
            .addInterceptor(interceptor)
            .build()
    }
}