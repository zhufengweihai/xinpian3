package uni.zf.xinpian.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.utils.ImageLoadUtil

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TagData>() {
    override fun areItemsTheSame(oldItem: TagData, newItem: TagData): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: TagData, newItem: TagData): Boolean {
        return oldItem == newItem
    }
}

open class EvenVideoListAdapter : PagingDataAdapter<TagData, EvenVideoListAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    protected open fun getHeight(resources: Resources): Int {
        return resources.getDimensionPixelSize(R.dimen.v_image_height)
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_image)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val labelView: TextView = itemView.findViewById(R.id.label_view)
        private val statusView: TextView = itemView.findViewById(R.id.status_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)

        init {
            itemView.clipToOutline = true
            itemView.layoutParams.height = getHeight(itemView.resources)
            itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        fun bind(video: TagData) {
            ImageLoadUtil.loadImagesWithDomain(imageView, video.path)
            scoreView.text = video.score
            statusView.text = video.mask
            nameView.text = video.title
            updateLabelView(labelView, video)
            itemView.setOnClickListener { toPlay(it.context, video) }
        }

        private fun updateLabelView(labelView: TextView, video: TagData) {
            val labelText = when (video.definition) {
                1 -> "高清"
                3 -> "热门"
                else -> ""
            }
            labelView.visibility = if (labelText.isEmpty()) View.GONE else View.VISIBLE
            labelView.text = labelText
        }

        private fun toPlay(context: Context, video: TagData) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.KEY_VIDEO_ID, video.id)
            }
            context.startActivity(intent)
        }
    }
}