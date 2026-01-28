package uni.zf.xinpian.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.utils.ImageLoadUtil.loadImages

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoData>() {
    override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
        return oldItem == newItem
    }
}
class CategoryVideoAdapter : PagingDataAdapter<VideoData, CategoryVideoAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(position,it) }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvLabel: TextView = itemView.findViewById(R.id.tv_label)
        private val tvInfo: TextView = itemView.findViewById(R.id.tv_info)

        fun bind(number:Int, video: VideoData) {
            tvNumber.text = (number+1).toString()
            loadImages(ivImage, video.thumbnail)
            tvTitle.text = video.title
            tvLabel.text = video.categoryString()
            tvInfo.text = video.score + "/" + video.year + "/" + video.actorsString()
            itemView.setOnClickListener { 

            }
        }
    }
}