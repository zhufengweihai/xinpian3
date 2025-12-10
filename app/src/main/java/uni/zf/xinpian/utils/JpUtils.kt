package uni.zf.xinpian.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private val jpUrlPrefix = generateJpUrlPrefix()
private const val version = "417"
private const val defaultSecret = "0sD4gjkMdbnsYp5k4K0oB5MGMggyp9UP"
val imgDomainUrl = "https://${jpUrlPrefix}.zxbwv.com/api/resourceDomainConfig"
val fenleiUrl = "https://${jpUrlPrefix}.zxbwv.com/api/term/home_fenlei"
val initUrl = "https://${jpUrlPrefix}.zxbwv.com/api/v2/sys/init"

fun generateSignature(timestamp: String, secret: String = defaultSecret): String {
    return calcMD5("$version$timestamp$secret")
}

fun generateJpUrlPrefix(): String {
    val charPool = ('0'..'9') + ('a'..'z') + ('A'..'Z')
    return buildString {
        repeat(6) {
            append(charPool.random())
        }
    }
}

fun createHeaders(signature: String = defaultSecret, userAgent: String): Map<String, String> {
    return mapOf(
        "Host" to "${jpUrlPrefix}.zxbwv.com",
        "timestamp" to (System.currentTimeMillis() / 1000).toString(),
        "signature" to signature,
        "User-Agent" to userAgent,
        "version" to version
    )
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