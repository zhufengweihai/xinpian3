package uni.zf.xinpian.utils

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun ImageView.loadImages(imageUrls: List<String>, index: Int = 0) {
    if (imageUrls.isEmpty() || index >= imageUrls.size) {
        return
    }
    Glide.with(context)
        .load(imageUrls[index])
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                Handler(Looper.getMainLooper()).post {
                    this@loadImages.loadImages(imageUrls, index + 1)
                }
                return true
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}