package uni.zf.xinpian.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.bumptech.glide.Glide
import uni.zf.xinpian.category.VideoListAdapter
import uni.zf.xinpian.data.model.TagData
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.databinding.ViewTagDataBinding

class TagDataView (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = ViewTagDataBinding.inflate(LayoutInflater.from(context), this)

    fun setTagData(tagData: TagData) {
        binding.tagTextView.text = tagData.name
        if(tagData.cover.isNotEmpty())
            Glide.with(this).load(tagData.cover).into(binding.coverView)
        setupVideoListView(tagData.videoList)
    }

    private fun setupVideoListView(videoList: List<VideoBrief>) {
        val adapter = VideoListAdapter(videoList)
        binding.videoListView.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.videoListView.adapter = adapter
    }
}