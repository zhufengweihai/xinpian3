package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.databinding.ItemRankingBinding
import uni.zf.xinpian.json.model.RankingItem
import uni.zf.xinpian.utils.ImageLoadUtil.loadImages

class RankingAdapter(private val items: List<RankingItem>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvTitle.text = item.title
            tvCast.text = "主演：${item.actors.joinToString(", ")}"
            tvDesc.text = item.description
            tvHot.text = "🔥 热度：${item.hotSort}" // Placeholder for hotness
            loadImages(ivPoster, item.posterPath)
            val rank = position + 1
            if (rank <= 3) {
                ivRankMedal.visibility = View.VISIBLE
                tvRankNum.visibility = View.GONE
            } else {
                ivRankMedal.visibility = View.GONE
                tvRankNum.visibility = View.VISIBLE
                tvRankNum.text = rank.toString()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}