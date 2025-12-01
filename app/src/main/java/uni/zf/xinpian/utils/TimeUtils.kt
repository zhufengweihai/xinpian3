package uni.zf.xinpian.utils

import android.annotation.SuppressLint

object TimeUtils {

    /**
     * 格式化毫秒数为 hh:mm:ss 这样的时间格式。
     *
     * @param ms 毫秒数
     * @return 格式化后的字符串
     */
    @JvmStatic
    fun formatMs(ms: Long): String {
        val totalSeconds = (ms / 1000).toInt()
        return convertToHHMMSS(totalSeconds)
    }

    /**
     * 将时间字符串转换为总秒数。
     *
     * @param time 时间字符串，格式可以是 hh:mm:ss 或 mm:ss
     * @return 总秒数
     */
    fun convertToSeconds(time: String): Int {
        val units = time.split(":").map { it.toInt() }
        return when (units.size) {
            3 -> units[0] * 3600 + units[1] * 60 + units[2]
            2 -> units[0] * 60 + units[1]
            else -> units[0]
        }
    }

    /**
     * 在给定时间字符串上增加秒数，并返回新的时间字符串。
     *
     * @param time 时间字符串，格式可以是 hh:mm:ss 或 mm:ss
     * @param secondsToAdd 要增加的秒数
     * @return 新的时间字符串
     */
    @SuppressLint("DefaultLocale")
    fun addSeconds(time: String, secondsToAdd: Int): String {
        val totalSeconds = (convertToSeconds(time) + secondsToAdd).coerceAtLeast(0)
        return convertToHHMMSS(totalSeconds)
    }

    /**
     * 将总秒数转换为 hh:mm:ss 格式的字符串。
     *
     * @param totalSeconds 总秒数
     * @return 格式化后的时间字符串
     */
    @SuppressLint("DefaultLocale")
    fun convertToHHMMSS(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}