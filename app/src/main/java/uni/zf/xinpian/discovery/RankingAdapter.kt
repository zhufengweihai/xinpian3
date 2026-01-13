package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.databinding.ItemRankingBinding
import uni.zf.xinpian.json.model.VideoData

class RankingAdapter(private val items: List<VideoData>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.binding.apply {
            tvTitle.text = item.title
            tvCast.text = "主演：${item.actorsString()}"
            tvDesc.text = item.description
            tvHot.text = "🔥 热度：${(10000..30000).random()}" // Placeholder for hotness

            Glide.with(context).load(item.thumbnail).into(ivPoster)

            val rank = position + 1
            if (rank <= 3) {
                ivRankMedal.visibility = View.VISIBLE
                tvRankNum.visibility = View.GONE
                // You would typically have drawable resources for 1, 2, 3 medals
                // ivRankMedal.setImageResource(when(rank) {
                //     1 -> R.drawable.medal_1
                //     2 -> R.drawable.medal_2
                //     else -> R.drawable.medal_3
                // })
            } else {
                ivRankMedal.visibility = View.GONE
                tvRankNum.visibility = View.VISIBLE
                tvRankNum.text = rank.toString()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}