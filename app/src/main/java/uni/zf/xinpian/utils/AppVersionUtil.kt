package uni.zf.xinpian.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat

/**
 * 仅用于获取当前 App 自身的版本信息
 */
object AppVersionUtil {
    /**
     * 获取自身版本名称（如 1.2.3）
     */
    fun getVersionName(context: Context): String {
        return try {
            val packageInfo = getPackageInfo(context)
            packageInfo?.versionName ?: "未知版本"
        } catch (e: Exception) {
            "未知版本"
        }
    }

    /**
     * 获取自身版本号（数字，如 10203）
     */
    fun getVersionCode(context: Context): Long {
        return try {
            val packageInfo = getPackageInfo(context)
            packageInfo?.let { PackageInfoCompat.getLongVersionCode(it) } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 私有方法：获取自身 App 的 PackageInfo
     */
    private fun getPackageInfo(context: Context): PackageInfo? {
        val packageManager = context.packageManager
        val packageName = context.packageName // 自身包名，无需手动写死
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 适配
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            // 低版本兼容
            packageManager.getPackageInfo(packageName, 0)
        }
    }
}