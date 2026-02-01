package uni.zf.xinpian.discovery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_VIDEO_ID
import uni.zf.xinpian.json.model.RankingItem
import uni.zf.xinpian.play.PlayActivity
import uni.zf.xinpian.utils.ImageLoadUtil.loadImages

class RankListAdapter(private var items: List<RankingItem> = listOf()) :
    RecyclerView.Adapter<RankListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_rank_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<RankingItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvActors: TextView = itemView.findViewById(R.id.tv_actors)
        private val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
        private val tvHot: TextView = itemView.findViewById(R.id.tv_hot)
        private val tvNumber: TextView = itemView.findViewById(R.id.tv_number)

        @SuppressLint("SetTextI18n")
        fun bind(number: Int, item: RankingItem) {
            loadImages(ivImage, item.posterPath)
            tvTitle.text = item.title
            tvActors.text = "主演：" + item.actorsString()
            tvDesc.text = item.description
            tvHot.text = "热度：" + item.hotSort.toString()
            tvNumber.text = (number + 1).toString()
            itemView.setOnClickListener { toPlay(itemView.context, item.id) }
        }

        private fun toPlay(context: Context, id: Int) {
            val intent = Intent(context, PlayActivity::class.java).apply {
                putExtra(ARG_VIDEO_ID, id)
            }
            context.startActivity(intent)
        }
    }
}