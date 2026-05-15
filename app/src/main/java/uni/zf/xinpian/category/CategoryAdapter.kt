package uni.zf.xinpian.category

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.GYLM_URL
import uni.zf.xinpian.databinding.ItemCategoryAdBinding
import uni.zf.xinpian.databinding.ItemCategoryDyTagBinding
import uni.zf.xinpian.databinding.ItemCategorySlideBinding
import uni.zf.xinpian.databinding.ItemCategoryTagsBinding
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.view.HorizontalItemDecoration

class CategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SLIDE = 0
        private const val TYPE_TAGS = 1
        private const val TYPE_AD = 2
        private const val TYPE_DY_TAG = 3
    }

    private var slideData: List<SlideData> = emptyList()
    private var customTags: List<CustomTag> = emptyList()
    private var dyTags: List<DyTag> = emptyList()

    // 缓存 CustomTagAdapter 避免重复创建
    private val customTagAdapter = CustomTagAdapter()

    fun updateSlideData(data: List<SlideData>) {
        if (this.slideData == data) return
        this.slideData = data
        notifyItemChanged(0)
    }

    fun updateCustomTags(tags: List<CustomTag>) {
        if (this.customTags == tags) return
        this.customTags = tags
        notifyItemChanged(1)
    }

    fun updateDyTags(tags: List<DyTag>) {
        if (this.dyTags == tags) return
        val oldSize = this.dyTags.size
        this.dyTags = tags
        val headerCount = 3 // slide + tags + ad
        if (oldSize == 0) {
            notifyItemRangeInserted(headerCount, tags.size)
        } else if (oldSize == tags.size) {
            notifyItemRangeChanged(headerCount, tags.size)
        } else {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        // slide(1) + tags(1) + ad(1) + dyTags(N)
        return 3 + dyTags.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_SLIDE
            1 -> TYPE_TAGS
            2 -> TYPE_AD
            else -> TYPE_DY_TAG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SLIDE -> SlideViewHolder(
                ItemCategorySlideBinding.inflate(inflater, parent, false)
            )
            TYPE_TAGS -> TagsViewHolder(
                ItemCategoryTagsBinding.inflate(inflater, parent, false),
                customTagAdapter
            )
            TYPE_AD -> AdViewHolder(
                ItemCategoryAdBinding.inflate(inflater, parent, false)
            )
            TYPE_DY_TAG -> DyTagViewHolder(
                ItemCategoryDyTagBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SlideViewHolder -> holder.bind(slideData)
            is TagsViewHolder -> holder.bind(customTags)
            is AdViewHolder -> holder.bind()
            is DyTagViewHolder -> holder.bind(dyTags[position - 3])
        }
    }

    // 防止 header 类型的 ViewHolder 被回收后重复初始化导致界面乱跳
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class SlideViewHolder(
        private val binding: ItemCategorySlideBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(slideData: List<SlideData>) {
            if (slideData.isNotEmpty()) {
                binding.slideView.setVideoList(slideData)
            }
        }
    }

    class TagsViewHolder(
        private val binding: ItemCategoryTagsBinding,
        private val adapter: CustomTagAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        private var isInitialized = false

        fun bind(customTags: List<CustomTag>) {
            if (!isInitialized) {
                val context = binding.root.context
                binding.tagListView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.tagListView.addItemDecoration(
                    HorizontalItemDecoration(
                        context.resources.getDimensionPixelSize(R.dimen.list_item_space)
                    )
                )
                binding.tagListView.adapter = adapter
                isInitialized = true
            }
            if (customTags.isNotEmpty()) {
                adapter.updateCustomTagList(customTags)
                binding.root.visibility = View.VISIBLE
            } else {
                binding.root.visibility = View.GONE
            }
        }
    }

    class AdViewHolder(
        private val binding: ItemCategoryAdBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.adImageView.setOnClickListener {
                it.context.startActivity(Intent(Intent.ACTION_VIEW, GYLM_URL.toUri()))
            }
        }
    }

    class DyTagViewHolder(
        private val binding: ItemCategoryDyTagBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dyTag: DyTag) {
            binding.tagDataView.setTagData(dyTag)
        }
    }
}
