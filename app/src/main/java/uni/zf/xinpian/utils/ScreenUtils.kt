package uni.zf.xinpian.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.core.content.getSystemService
import kotlin.math.roundToInt

fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).roundToInt()
}

private fun getDisplayMetrics(context: Context): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    val windowManager = context.getSystemService<WindowManager>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager?.currentWindowMetrics
        val bounds = windowMetrics?.bounds
        displayMetrics.widthPixels = bounds?.width() ?: 0
        displayMetrics.heightPixels = bounds?.height() ?: 0
    } else {
        @Suppress("DEPRECATION")
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    }

    return displayMetrics
}

fun getWidth(context: Context): Int {
    return getDisplayMetrics(context).widthPixels
}

fun getHeight(context: Context): Int {
    return getDisplayMetrics(context).heightPixels
}

fun isInRight(context: Context, xPos: Int): Boolean {
    return xPos > getWidth(context) / 2
}

fun isInLeft(context: Context, xPos: Int): Boolean {
    return xPos < getWidth(context) / 2
}

fun isInRight(view: View, xPos: Int): Boolean {
    return xPos > view.measuredWidth / 2
}

fun isInLeft(view: View, xPos: Int): Boolean {
    return xPos < view.measuredWidth / 2
}