package uni.zf.xinpian.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object NetworkTypeUtils {

    /**
     * 判断当前网络类型
     * @return 0:无网络 1:WiFi 2:移动数据（4G/5G/3G等）
     */
    fun getNetworkType(context: Context): Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Android 21（Lollipop）及以上推荐使用NetworkCapabilities
        // 获取当前活跃网络
        val network: Network? = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return 0 // 无网络

        return when {
            // 判断是否是WiFi
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 1
            // 判断是否是移动数据（包含5G/4G/3G/2G）
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 2
            // 其他网络类型（如以太网、VPN等）
            else -> 0
        }
    }

    /**
     * 简化判断：是否是WiFi
     */
    fun isWifiConnected(context: Context): Boolean {
        return getNetworkType(context) == 1
    }

    /**
     * 简化判断：是否是移动数据
     */
    fun isMobileDataConnected(context: Context): Boolean {
        return getNetworkType(context) == 2
    }

    /**
     * 判断是否有网络连接（无论WiFi/移动数据）
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return getNetworkType(context) != 0
    }
}