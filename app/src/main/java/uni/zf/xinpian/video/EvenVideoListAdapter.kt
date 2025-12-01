package uni.zf.xinpian.video

import android.content.res.Resources
import uni.zf.xinpian.R

open class EvenVideoListAdapter : VideoListAdapter() {
    override fun even(): Boolean {
        return true
    }

    override fun getHeight(resources: Resources): Int {
        return resources.getDimensionPixelSize(R.dimen.v_image_height)
    }
}