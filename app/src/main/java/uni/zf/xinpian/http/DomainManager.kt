package uni.zf.xinpian.http

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Request
import uni.zf.xinpian.data.AppConst.DOMAIN_URL
import uni.zf.xinpian.utils.prefs

/**
 * 域名管理器：从远程获取域名列表，支持域名故障自动切换
 * - 启动时从 DOMAIN_URL 获取域名列表
 * - 请求失败时自动切换到下一个可用域名
 * - 域名列表持久化到 SharedPreferences
 */
object DomainManager {
    private const val TAG = "DomainManager"
    private const val KEY_DOMAINS = "domain_list"
    private const val KEY_CURRENT_INDEX = "current_domain_index"
    private const val DEFAULT_DOMAIN = "zxfmj.com"

    private val mutex = Mutex()

    @Volatile
    private var domainList: List<String> = listOf(DEFAULT_DOMAIN)

    @Volatile
    private var currentIndex: Int = 0

    /**
     * 获取当前使用的域名
     */
    val currentDomain: String
        get() = domainList.getOrElse(currentIndex) { domainList.firstOrNull() ?: DEFAULT_DOMAIN }

    /**
     * 初始化：从本地缓存恢复域名列表
     */
    fun init(context: Context) {
        val prefs = context.prefs
        val cached = prefs.getString(KEY_DOMAINS, null)
        if (!cached.isNullOrBlank()) {
            domainList = cached.split(",").filter { it.isNotBlank() }
        }
        currentIndex = prefs.getInt(KEY_CURRENT_INDEX, 0)
        if (currentIndex >= domainList.size) currentIndex = 0
    }

    /**
     * 从远程获取域名列表并更新
     */
    suspend fun fetchDomains(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(DOMAIN_URL)
                    .get()
                    .build()
                val response = OkHttpManager.instance.newCall(request).execute()
                val body = response.body.string()
                response.close()
                if (body.isNotBlank()) {
                    val domains = body.trim().split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    if (domains.isNotEmpty()) {
                        mutex.withLock {
                            domainList = domains
                            // 如果当前索引超出新列表范围，重置为0
                            if (currentIndex >= domainList.size) {
                                currentIndex = 0
                            }
                        }
                        // 持久化
                        context.prefs.edit {
                            putString(KEY_DOMAINS, domains.joinToString(","))
                            putInt(KEY_CURRENT_INDEX, currentIndex)
                        }
                        Log.d(TAG, "域名列表更新成功: $domains, 当前使用: ${currentDomain}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取域名列表失败: ${e.message}")
            }
        }
    }

    /**
     * 切换到下一个域名，返回是否还有可用域名
     */
    suspend fun switchToNextDomain(context: Context): Boolean {
        return mutex.withLock {
            if (domainList.size <= 1) {
                Log.w(TAG, "没有更多可用域名")
                return@withLock false
            }
            currentIndex = (currentIndex + 1) % domainList.size
            context.prefs.edit { putInt(KEY_CURRENT_INDEX, currentIndex) }
            Log.d(TAG, "切换域名到: ${currentDomain} (index=$currentIndex)")
            true
        }
    }

    /**
     * 将URL中的域名替换为当前活跃域名
     * 例如: https://abc123.zxfmj.com/api/xxx -> https://abc123.feicifang.com/api/xxx
     */
    fun replaceUrlDomain(url: String): String {
        // 匹配所有已知的可能域名并替换为当前域名
        val domain = currentDomain
        // 替换 zxfmj.com 以及域名列表中的所有域名
        var result = url.replace("zxfmj.com", domain)
        for (d in domainList) {
            if (d != domain) {
                result = result.replace(d, domain)
            }
        }
        return result
    }

    /**
     * 构建完整的API URL（使用当前域名）
     * @param prefix URL前缀（如随机生成的6位字符）
     * @param path API路径（如 /api/video/detailv2?id=123）
     */
    fun buildUrl(prefix: String, path: String): String {
        return "https://${prefix}.${currentDomain}${path}"
    }
}
