package uni.zf.xinpian.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun TextView.highlightText(
    originalStr: String,
    targetStr: String,
    ignoreCase: Boolean = false
) {
    if (originalStr.isEmpty() || targetStr.isEmpty()) {
        text = originalStr
        return
    }

    val spannable = SpannableString(originalStr)
    val startIndex = originalStr.indexOf(targetStr, 0, ignoreCase)
    if (startIndex != -1) {
        val endIndex = startIndex + targetStr.length
        spannable.setSpan(ForegroundColorSpan(Color.RED), startIndex, endIndex, SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = spannable
}