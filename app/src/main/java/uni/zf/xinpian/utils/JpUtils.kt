package uni.zf.xinpian.utils

import android.content.Context
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.AppConst.DEFAULT_SECRET
import uni.zf.xinpian.data.AppConst.host
import uni.zf.xinpian.data.AppConst.PACKAGE_NAME
import uni.zf.xinpian.data.AppConst.VERSION
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

fun createHeaders(context: Context, url: String = ""): Map<String, String> {
    val timestamp = (System.currentTimeMillis() / 1000).toString()
    val signature = generateSignature(timestamp, AppData.getInstance(context).secret)
    return mapOf(
        "Host" to if (url.isEmpty()) host else url.substringAfter("://").substringBefore("/"),
        "timestamp" to timestamp,
        "signature" to signature,
        "User-Agent" to AppData.getInstance(context).userAgent,
        "version" to VERSION,
        "X-Requested-With" to PACKAGE_NAME
    )
}

fun createHeaders(secret: String, userAgent: String): Map<String, String> {
    val timestamp = (System.currentTimeMillis() / 1000).toString()
    val signature = generateSignature(timestamp, secret)
    return mapOf(
        "Host" to host,
        "timestamp" to timestamp,
        "signature" to signature,
        "User-Agent" to userAgent,
        "version" to VERSION,
        "X-Requested-With" to PACKAGE_NAME
    )
}

fun generateSignature(timestamp: String, secret: String = DEFAULT_SECRET): String {
    return calcMD5("$VERSION$timestamp$secret")
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