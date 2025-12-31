package uni.zf.xinpian.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import androidx.core.content.edit
import androidx.startup.Initializer
import uni.zf.xinpian.data.AppConst.DEFAULT_IMG_DOMAINS
import uni.zf.xinpian.data.AppConst.DEFAULT_SECRET
import uni.zf.xinpian.data.AppConst.KEY_IMG_DOMAINS
import uni.zf.xinpian.data.AppConst.KEY_SECRET
import uni.zf.xinpian.data.AppConst.NETWORK_MOBILE
import uni.zf.xinpian.data.AppConst.NETWORK_WIFI
import uni.zf.xinpian.data.AppConst.USER_AGENT_SUFFIX
import uni.zf.xinpian.utils.NetworkTypeUtils
import uni.zf.xinpian.utils.prefs

class AppData private constructor(val context: Context) {
    val userAgent = initUserAgent()
    var secret = context.prefs.getString(KEY_SECRET, DEFAULT_SECRET)!!
        set(value) {
            context.prefs.edit { putString(KEY_SECRET, value) }
        }
    var imgDomains = context.prefs.getString(KEY_IMG_DOMAINS, DEFAULT_IMG_DOMAINS)!!.split(",")
        set(value) {
            context.prefs.edit { putString(KEY_IMG_DOMAINS, value.joinToString(",")) }
        }

    private fun initUserAgent(): String {
        val networkType = if (NetworkTypeUtils.isWifiConnected(context)) NETWORK_WIFI else NETWORK_MOBILE
        return WebSettings.getDefaultUserAgent(context) + USER_AGENT_SUFFIX.format(networkType)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
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