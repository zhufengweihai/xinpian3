package uni.zf.xinpian.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_VIDEO_ID
import uni.zf.xinpian.json.model.SearchResultItem
import uni.zf.xinpian.play.PlayActivity
import uni.zf.xinpian.utils.ImageLoadUtil.loadImages

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchResultItem>() {
    override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
        return oldItem == newItem
    }
}
class SearchResultAdapter : PagingDataAdapter<SearchResultItem, SearchResultAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(position,it) }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvActors: TextView = itemView.findViewById(R.id.tv_actors)
        private val tvInfo: TextView = itemView.findViewById(R.id.tv_info)
        private val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)


        @SuppressLint("SetTextI18n")
        fun bind(number:Int, item: SearchResultItem) {
            loadImages(ivImage, item.thumbnail)
            tvTitle.text = item.title
            tvActors.text = item.actors.joinToString("/")
            tvInfo.text = item.info()
            tvDesc.text = item.description
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