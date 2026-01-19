package uni.zf.xinpian.discovery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.databinding.ItemSpecialBinding
import uni.zf.xinpian.json.model.Special
import uni.zf.xinpian.utils.ImageLoadUtil.loadImagesWithDomain

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
        getItem(position)?.let {
            val binding = holder.binding
            binding.tvTitle.text = it.title
            loadImagesWithDomain(binding.ivCover, it.coverUrl)
        }
    }
}