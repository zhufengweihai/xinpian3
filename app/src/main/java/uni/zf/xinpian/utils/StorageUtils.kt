package uni.zf.xinpian.utils

import android.annotation.SuppressLint
import android.os.Environment
import android.os.StatFs

private fun getStatFs(): StatFs? {
    return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        StatFs(Environment.getExternalStorageDirectory().path)
    } else {
        null
    }
}

private fun getMemorySize(getSize: (StatFs) -> Long): Long {
    return getStatFs()?.let(getSize) ?: -1
}

val totalExternalMemorySize: Long
    get() = getMemorySize { it.blockCountLong * it.blockSizeLong }

val availableExternalMemorySize: Long
    get() = getMemorySize { it.availableBlocksLong * it.blockSizeLong }

@SuppressLint("DefaultLocale")
fun formatSizeToGB(sizeInBytes: Long): String {
    val sizeInGB = sizeInBytes / (1024.0 * 1024.0 * 1024.0)
    return String.format("%.1f GB", sizeInGB)
}

@SuppressLint("DefaultLocale")
fun formatBytes(bytes: Long): String {
    val KB = 1_024L
    val MB = 1_048_576L
    val GB = 1_073_741_824L

    return when {
        bytes >= GB -> String.format("%.2f GB", bytes.toDouble() / GB)
        bytes >= MB -> String.format("%.2f MB", bytes.toDouble() / MB)
        else -> String.format("%.2f KB", bytes.toDouble() / KB)
    }
}

val formattedTotalExternalMemorySize: String
    get() = formatSizeToGB(totalExternalMemorySize)

val formattedAvailableExternalMemorySize: String
    get() = formatSizeToGB(availableExternalMemorySize)