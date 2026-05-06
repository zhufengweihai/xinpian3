package uni.zf.xinpian.utils

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

/**
 * 二维码扫描工具类
 */
object QrCodeScanner {

    private val hints = mapOf(
        DecodeHintType.TRY_HARDER to true,
        DecodeHintType.PURE_BARCODE to false
    )

    /**
     * 从Bitmap中识别二维码
     * @param bitmap 要识别的图片
     * @return 识别结果，失败返回null
     */
    fun scanQrCode(bitmap: Bitmap): String? {
        return tryDecodeQrCode(bitmap)
    }

    /**
     * 尝试多种方式识别二维码
     */
    private fun tryDecodeQrCode(bitmap: Bitmap): String? {
        // 策略1: 直接识别原图（TRY_HARDER 模式会花更多时间扫描整张图）
        decodeBitmap(bitmap)?.let { return it }

        // 策略2: 尝试图片的不同区域
        val regions = extractRegions(bitmap)
        for (region in regions) {
            decodeBitmap(region)?.let {
                if (region != bitmap) region.recycle()
                return it
            }
            if (region != bitmap) region.recycle()
        }

        // 策略3: 缩放图片后识别
        val scaledBitmaps = createScaledVersions(bitmap)
        for (scaled in scaledBitmaps) {
            decodeBitmap(scaled)?.let {
                scaled.recycle()
                return it
            }
            scaled.recycle()
        }

        return null
    }

    /**
     * 解码单个Bitmap
     */
    private fun decodeBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            reader.setHints(hints)
            reader.decodeWithState(binaryBitmap).text
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 提取图片的多个区域
     */
    private fun extractRegions(bitmap: Bitmap): List<Bitmap> {
        val regions = mutableListOf<Bitmap>()
        val width = bitmap.width
        val height = bitmap.height

        // 按比例裁剪不同区域 (startXRatio, startYRatio, widthRatio, heightRatio)
        val cropSpecs = listOf(
            floatArrayOf(0.1f, 0.1f, 0.8f, 0.8f),   // 中心80%
            floatArrayOf(0.0f, 0.0f, 0.5f, 0.5f),    // 左上
            floatArrayOf(0.5f, 0.0f, 0.5f, 0.5f),    // 右上
            floatArrayOf(0.0f, 0.5f, 0.5f, 0.5f),    // 左下
            floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f),    // 右下
            floatArrayOf(0.6f, 0.0f, 0.4f, 0.5f),    // 右上角小区域
            floatArrayOf(0.6f, 0.5f, 0.4f, 0.5f),    // 右下角小区域
            floatArrayOf(0.0f, 0.0f, 0.4f, 0.5f),    // 左上角小区域
            floatArrayOf(0.0f, 0.5f, 0.4f, 0.5f),    // 左下角小区域
        )

        for (spec in cropSpecs) {
            try {
                val startX = (width * spec[0]).toInt()
                val startY = (height * spec[1]).toInt()
                val regionWidth = (width * spec[2]).toInt().coerceAtMost(width - startX)
                val regionHeight = (height * spec[3]).toInt().coerceAtMost(height - startY)

                if (regionWidth > 50 && regionHeight > 50) {
                    regions.add(Bitmap.createBitmap(bitmap, startX, startY, regionWidth, regionHeight))
                }
            } catch (_: Exception) {
            }
        }

        return regions
    }

    /**
     * 创建不同缩放版本的图片
     */
    private fun createScaledVersions(bitmap: Bitmap): List<Bitmap> {
        val scaledList = mutableListOf<Bitmap>()
        val scales = listOf(1.5f, 2.0f)

        for (scale in scales) {
            try {
                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()
                if (newWidth > 0 && newHeight > 0) {
                    scaledList.add(Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true))
                }
            } catch (_: Exception) {
            }
        }

        return scaledList
    }
}
