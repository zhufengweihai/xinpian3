package uni.zf.xinpian.utils

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

/**
 * 二维码扫描工具类
 */
object QrCodeScanner {

    /**
     * 从Bitmap中识别二维码
     * @param bitmap 要识别的图片
     * @return 识别结果，失败返回null
     */
    fun scanQrCode(bitmap: Bitmap): String? {
        // 尝试多种识别策略
        return tryDecodeQrCode(bitmap)
    }

    /**
     * 尝试多种方式识别二维码
     */
    private fun tryDecodeQrCode(bitmap: Bitmap): String? {
        // 策略1: 直接识别原图
        decodeBitmap(bitmap)?.let { return it }

        // 策略2: 尝试图片的不同区域（九宫格）
        val regions = extractRegions(bitmap)
        for (region in regions) {
            decodeBitmap(region)?.let { return it }
        }

        // 策略3: 缩放图片后识别
        val scaledBitmaps = createScaledVersions(bitmap)
        for (scaled in scaledBitmaps) {
            decodeBitmap(scaled)?.let { return it }
        }

        // 策略4: 对图片进行二值化处理后识别
        val processedBitmap = enhanceContrast(bitmap)
        decodeBitmap(processedBitmap)?.let { return it }

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
            MultiFormatReader().decode(binaryBitmap).text
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 提取图片的多个区域（九宫格裁剪）
     */
    private fun extractRegions(bitmap: Bitmap): List<Bitmap> {
        val regions = mutableListOf<Bitmap>()
        val width = bitmap.width
        val height = bitmap.height
        
        // 定义裁剪比例：中心区域、四角、边缘等
        val cropRatios = listOf(
            Triple(0.2f, 0.2f, 0.6f),  // 中心60%
            Triple(0.0f, 0.0f, 0.5f),   // 左上
            Triple(0.5f, 0.0f, 0.5f),   // 右上
            Triple(0.0f, 0.5f, 0.5f),   // 左下
            Triple(0.5f, 0.5f, 0.5f),   // 右下
            Triple(0.0f, 0.0f, 1.0f),   // 左半边
            Triple(0.5f, 0.0f, 0.5f),   // 右半边
            Triple(0.0f, 0.0f, 0.33f),  // 上三分之一
            Triple(0.0f, 0.67f, 0.33f)  // 下三分之一
        )

        for ((startXRatio, startYRatio, sizeRatio) in cropRatios) {
            try {
                val startX = (width * startXRatio).toInt()
                val startY = (height * startYRatio).toInt()
                val regionSize = (Math.min(width, height) * sizeRatio).toInt()
                
                val regionWidth = Math.min(regionSize, width - startX)
                val regionHeight = Math.min(regionSize, height - startY)
                
                if (regionWidth > 0 && regionHeight > 0) {
                    val region = Bitmap.createBitmap(bitmap, startX, startY, regionWidth, regionHeight)
                    regions.add(region)
                }
            } catch (e: Exception) {
                // 忽略裁剪错误
            }
        }

        return regions
    }

    /**
     * 创建不同缩放版本的图片
     */
    private fun createScaledVersions(bitmap: Bitmap): List<Bitmap> {
        val scaledList = mutableListOf<Bitmap>()
        val scales = listOf(0.5f, 0.75f, 1.5f, 2.0f)
        
        for (scale in scales) {
            try {
                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()
                if (newWidth > 0 && newHeight > 0) {
                    val scaled = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                    scaledList.add(scaled)
                }
            } catch (e: Exception) {
                // 忽略缩放错误
            }
        }
        
        return scaledList
    }

    /**
     * 增强图片对比度（简单的二值化处理）
     */
    private fun enhanceContrast(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val enhanced = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        // 转换为灰度并二值化
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            
            // 计算灰度值
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            
            // 二值化阈值
            val value = if (gray > 128) 255 else 0
            
            pixels[i] = (0xFF shl 24) or (value shl 16) or (value shl 8) or value
        }
        
        enhanced.setPixels(pixels, 0, width, 0, 0, width, height)
        return enhanced
    }
}
