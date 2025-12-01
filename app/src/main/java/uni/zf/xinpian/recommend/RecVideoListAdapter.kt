package uni.zf.xinpian.recommend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.utils.loadImages
import java.util.regex.Pattern

class RecVideoListAdapter(private val videoList: List<Video>, private val horizontal: Boolean) :
    Adapter<RecVideoListAdapter.ViewHolder>() {

    private val pattern: Pattern = Pattern.compile("TC|tC|TS|枪版|抢先|尝鲜")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view, horizontal)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size

    private fun updateLabelView(labelView: TextView, video: Video) {
        val (labelText, labelColor) = when {
            video.isMovie() && pattern.matcher(video.status).find() -> "普清" to getColor(labelView.context, R.color.main)
            video.isMovie() -> "高清" to Color.RED
            video.hotness > 1000 -> "火热" to Color.RED
            else -> "" to Color.TRANSPARENT
        }
        labelView.text = labelText
        labelView.setBackgroundColor(labelColor)
    }

    inner class ViewHolder(itemView: View, horizontal: Boolean) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_image)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val labelView: TextView = itemView.findViewById(R.id.label_view)
        private val statusView: TextView = itemView.findViewById(R.id.status_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)

        init {
            itemView.clipToOutline = true
            if (!horizontal) {
                val height = itemView.resources.getDimensionPixelSize(R.dimen.image_height)
                itemView.layoutParams.height = height
            }
        }

        fun bind(video: Video) {
            Glide.with(imageView).load(video.image).into(imageView)
            scoreView.text = video.dbScore
            statusView.text = video.status
            nameView.text = video.name
            updateLabelView(labelView, video)
            itemView.setOnClickListener { toPlay(it.context, video) }
        }
    }

    companion object {
        private fun toPlay(context: Context, video: Video) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.KEY_VIDEO_ID, video.id)
            }
            context.startActivity(intent)
        }
    }
}