package uni.zf.xinpian.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import uni.zf.xinpian.common.AppData

private val MAIN_HANDLER = Handler(Looper.getMainLooper())

/**
 * 全局图片加载工具类 - Glide封装
 * 包含：单链接加载、图片列表失败重试下一个（基础版+增强版）
 */

object ImageLoadUtil {
    val headers =mapOf (
        "User-Agent" to "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Mobile Sa"
    )
    fun loadDoubanImage(imageView: ImageView, url: String) {
        val glideUrl = GlideUrl(
            url,
            LazyHeaders.Builder().apply {
                headers.forEach { (key, value) ->
                    addHeader(key, value)
                }
                addHeader("referer", url)
            }.build()
        )

        Glide.with(imageView.context)
            .load(glideUrl)
            .placeholder(android.R.color.transparent)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
            .into(imageView)
    }

    fun loadImages(imageView: ImageView, url: String) {
        val context = imageView.context
        if (url.startsWith("http")) {
            Glide.with(context)
                .load(url)
                .placeholder(android.R.color.transparent)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false)
                .into(imageView)
        } else {
            val imgList = AppData.getInstance(context).imgDomains.map { "https://$it$url" }
            loadImages(imageView, imgList)
        }
    }

    @SuppressLint("CheckResult")
    fun loadImages(imageView: ImageView, imgList: List<String>, currentIndex: Int = 0) {
        if (imgList.isEmpty() || currentIndex >= imgList.size) return
        val currentImgUrl = imgList[currentIndex]
        Glide.with(imageView.context)
            .load(currentImgUrl)
            .placeholder(android.R.color.transparent)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
            .listener(requestListener(imageView, imgList, currentIndex))
            .into(imageView)
    }

    private fun requestListener(
        imageView: ImageView,
        imgList: List<String>,
        currentIndex: Int
    ): RequestListener<Drawable> = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            if (isFirstResource) {
                MAIN_HANDLER.post { loadImages(imageView, imgList, currentIndex + 1) }
            }
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean = false
    }
}