package uni.zf.xinpian.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import androidx.core.content.edit
import androidx.startup.Initializer
import uni.zf.xinpian.data.AppConst.defaultImgDomains
import uni.zf.xinpian.data.AppConst.defaultSecret
import uni.zf.xinpian.data.AppConst.keyImgDomains
import uni.zf.xinpian.data.AppConst.keySecret
import uni.zf.xinpian.data.AppConst.networkMobile
import uni.zf.xinpian.data.AppConst.networkWifi
import uni.zf.xinpian.data.AppConst.userAgentSuffix
import uni.zf.xinpian.utils.NetworkTypeUtils
import uni.zf.xinpian.utils.prefs

class AppData private constructor(val context: Context) {
    val userAgent = initUserAgent()
    var secret = context.prefs.getString(keySecret, defaultSecret)!!
        set(value) {
            context.prefs.edit { putString(keySecret, value) }
        }
    var imgDomains = context.prefs.getString(keyImgDomains, defaultImgDomains)!!.split(",")
        set(value) {
            context.prefs.edit { putString(keyImgDomains, value.joinToString(",")) }
        }

    fun imgDomain() = "https://" + imgDomains.random()

    private fun initUserAgent(): String {
        val networkType = if (NetworkTypeUtils.isWifiConnected(context)) networkWifi else networkMobile
        return WebSettings.getDefaultUserAgent(context) + userAgentSuffix.format(networkType)
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