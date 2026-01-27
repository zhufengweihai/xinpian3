package uni.zf.xinpian.discovery

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.category.TagVideoListActivity
import uni.zf.xinpian.data.AppConst.ARG_DY_TAG
import uni.zf.xinpian.data.AppConst.ARG_TAG_TITLE
import uni.zf.xinpian.databinding.ItemSpecialBinding
import uni.zf.xinpian.json.model.Special
import uni.zf.xinpian.utils.ImageLoadUtil.loadImages

private val diffCallback = object : DiffUtil.ItemCallback<Special>() {
    override fun areItemsTheSame(oldItem: Special, newItem: Special): Boolean {
        return oldItem.topicId == newItem.topicId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Special, newItem: Special): Boolean {
        return oldItem == newItem
    }
}

class SpecialsAdapter() : PagingDataAdapter<Special, SpecialsAdapter.ViewHolder>(diffCallback) {

    class ViewHolder(val binding: ItemSpecialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSpecialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { special ->
            val binding = holder.binding
            binding.tvTitle.text = special.title
            loadImages(binding.ivCover, special.coverUrl)
            binding.root.setOnClickListener {
                val context = binding.root.context
                context.startActivity(
                Intent(context, TagVideoListActivity::class.java).apply {
                    putExtra(ARG_DY_TAG, special.topicId)
                    putExtra(ARG_TAG_TITLE, special.title)
                })
            }
        }
    }
}