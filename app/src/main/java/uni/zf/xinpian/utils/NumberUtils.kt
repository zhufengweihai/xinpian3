package uni.zf.xinpian.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun toPercent(num: Double): String {
    val percentage = num * 100
    return String.format("%.1f%%", percentage)
}

