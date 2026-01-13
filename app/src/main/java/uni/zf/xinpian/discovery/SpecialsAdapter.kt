package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.databinding.ItemSpecialBinding

data class SpecialItem(val title: String, val coverUrl: String)

class SpecialsAdapter(private val items: List<SpecialItem>) : RecyclerView.Adapter<SpecialsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSpecialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSpecialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvTitle.text = item.title
            Glide.with(holder.itemView.context).load(item.coverUrl).into(ivCover)
        }
    }

    override fun getItemCount(): Int = items.size
}