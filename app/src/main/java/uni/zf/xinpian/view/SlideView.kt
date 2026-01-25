package uni.zf.xinpian.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ViewBannerBinding
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.utils.dpToPx

class SlideView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = ViewBannerBinding.inflate(LayoutInflater.from(context), this)
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    fun setVideoList(videoList: List<SlideData>) {
        if (videoList.isEmpty()) return
        setupViewPager(videoList)
        setupIndicators(videoList.size)
        displayInLoop(videoList)
        updateNameView(videoList.first().title)
    }

    private fun setupViewPager(videoList: List<SlideData>) {
        binding.viewPager.adapter = SlideImageAdapter(videoList)
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateCurrentView(position, videoList)
        })
    }

    private fun setupIndicators(size: Int) {
        binding.indicatorGroup.removeAllViews()
        repeat(size) {
            val radioButton = RadioButton(context).apply {
                setButtonDrawable(R.drawable.selector_indicator)
                layoutParams = ViewGroup.LayoutParams(dpToPx(context, 20), dpToPx(context, 30))
                id = it
            }
            binding.indicatorGroup.addView(radioButton)
        }
    }

    private fun displayInLoop(videoList: List<SlideData>) {
        runnable?.let { handler.removeCallbacks(it) }
        runnable = Runnable {
            val currentItem = binding.viewPager.currentItem
            val nextItem = (currentItem + 1) % videoList.size
            updateCurrentView(nextItem, videoList)
            handler.postDelayed(runnable!!, 5000)
        }
        handler.postDelayed(runnable!!, 5000)
        binding.indicatorGroup.check(0)
    }

    private fun updateCurrentView(nextItem: Int, videoList: List<SlideData>) {
        binding.viewPager.setCurrentItem(nextItem, true)
        binding.indicatorGroup.check(nextItem)
        updateNameView(videoList[nextItem].title)
    }

    private fun updateNameView(name: String) {
        binding.nameView.text = name
    }
}