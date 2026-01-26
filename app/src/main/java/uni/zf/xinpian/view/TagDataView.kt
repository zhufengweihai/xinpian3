package uni.zf.xinpian.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import uni.zf.xinpian.category.TagVideoListActivity
import uni.zf.xinpian.category.VideoListAdapter
import uni.zf.xinpian.data.AppConst.ARG_DY_TAG
import uni.zf.xinpian.data.AppConst.ARG_TAG_TITLE
import uni.zf.xinpian.databinding.ViewTagDataBinding
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.utils.ImageLoadUtil

class TagDataView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = ViewTagDataBinding.inflate(LayoutInflater.from(context), this)

    fun setTagData(dyTag: DyTag) {
        binding.tagTextView.text = dyTag.name
        if (dyTag.cover.isNotEmpty()) {
            ImageLoadUtil.loadImages(binding.coverView, dyTag.cover)
            binding.coverView.visibility = VISIBLE
        } else {
            binding.coverView.visibility = GONE
        }
        binding.moreTextView.setOnClickListener {
            binding.moreTextView.context.startActivity(
                Intent(
                    binding.moreTextView.context,
                    TagVideoListActivity::class.java
                ).apply {
                    putExtra(ARG_DY_TAG, dyTag.id)
                    putExtra(ARG_TAG_TITLE, dyTag.name)
                })
        }
        setupVideoListView(dyTag.dataList)
    }

    private fun setupVideoListView(tagDataList: List<TagData>) {
        val adapter = VideoListAdapter(tagDataList)
        binding.videoListView.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.videoListView.adapter = adapter
        binding.videoListView.addItemDecoration(SpaceItemDecoration(context))
    }
}