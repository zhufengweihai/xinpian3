package uni.zf.xinpian.utils

import android.content.Context
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.AppConst.defaultSecret
import uni.zf.xinpian.data.AppConst.host
import uni.zf.xinpian.data.AppConst.packageName
import uni.zf.xinpian.data.AppConst.version
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun generateJpUrlPrefix(): String {
    val charPool = ('0'..'9') + ('a'..'z')
    return buildString {
        repeat(6) {
            append(charPool.random())
        }
    }
}

fun createHeaders(context: Context): Map<String, String> {
    val timestamp = (System.currentTimeMillis() / 1000).toString()
    val signature = generateSignature(timestamp, AppData.getInstance(context).secret)
    return mapOf(
        "Host" to host,
        "timestamp" to timestamp,
        "signature" to signature,
        "User-Agent" to AppData.getInstance(context).userAgent,
        "version" to version,
        "X-Requested-With" to packageName,
        "Connection" to "keep-alive"
    )
}

fun generateSignature(timestamp: String, secret: String = defaultSecret): String {
    return calcMD5("$version$timestamp$secret")
}

fun calcMD5(input: String?, toUpperCase: Boolean = false): String {
    val inputBytes = input?.toByteArray(StandardCharsets.UTF_8) ?: byteArrayOf()
    return try {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(inputBytes)
        val sb = StringBuilder()
        for (b in digest) {
            val hex = String.format("%02x", b)
            sb.append(hex)
        }
        if (toUpperCase) sb.toString().uppercase() else sb.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}