package uni.zf.xinpian.video

import android.content.res.Resources
import uni.zf.xinpian.R

class LargeVideoListAdapter : EvenVideoListAdapter() {
    override fun getHeight(resources: Resources): Int {
        return resources.getDimensionPixelSize(R.dimen.large_image_height)
    }
}