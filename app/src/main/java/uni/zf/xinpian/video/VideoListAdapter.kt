package uni.zf.xinpian.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.utils.loadImages
import java.util.regex.Pattern

open class VideoListAdapter : PagingDataAdapter<Video, VideoListAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    protected open fun getHeight(resources: Resources): Int {
        return resources.getDimensionPixelSize(R.dimen.image_height)
    }

    protected open fun even(): Boolean {
        return false
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pattern: Pattern = Pattern.compile("TC|tC|TS|枪版|抢先|尝鲜")
        private val imageView: ImageView = itemView.findViewById(R.id.video_image)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val labelView: TextView = itemView.findViewById(R.id.label_view)
        private val statusView: TextView = itemView.findViewById(R.id.status_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)
        private val colorMain = ContextCompat.getColor(itemView.context, R.color.main)

        init {
            itemView.clipToOutline = true
            itemView.layoutParams.height = getHeight(itemView.resources)
            if (even()) {
                itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        fun bind(video: Video) {
            Glide.with(imageView).load(video.image).into(imageView)
            scoreView.text = video.dbScore
            statusView.text = video.status
            nameView.text = video.name
            updateLabelView(video)
            itemView.setOnClickListener { toPlay(it.context, video) }
        }

        private fun updateLabelView(video: Video) {
            val (labelText, labelColor) = when {
                video.isMovie() && pattern.matcher(video.status).find() -> "普清" to colorMain
                video.isMovie() -> "高清" to Color.RED
                video.hotness > 1000 -> "火热" to Color.RED
                else -> "" to Color.TRANSPARENT
            }
            labelView.text = labelText
            labelView.setBackgroundColor(labelColor)
        }

        private fun toPlay(context: Context, video: Video) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.KEY_VIDEO_ID, video.id)
            }
            context.startActivity(intent)
        }
    }
}