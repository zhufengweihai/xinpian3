package uni.zf.xinpian.category

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import uni.zf.xinpian.App
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.player.PlayerActivity

class VideoListAdapter(val imagDomain: String, private val videoList: List<VideoBrief>) :
    Adapter<VideoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_video, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size

    private fun updateLabelView(labelView: TextView, video: VideoBrief) {
        val labelText = when (video.definition) {
            1 -> "热门"
            3 -> "高清"
            else -> ""
        }
        labelView.visibility = if (labelText.isEmpty()) View.GONE else View.VISIBLE
        labelView.text = labelText
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_image)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val labelView: TextView = itemView.findViewById(R.id.label_view)
        private val statusView: TextView = itemView.findViewById(R.id.status_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)

        init {
            itemView.clipToOutline = true
        }

        fun bind(video: VideoBrief) {
            Glide.with(imageView).load(imagDomain + video.image).into(imageView)
            scoreView.text = video.score
            statusView.text = video.mask
            nameView.text = video.title
            updateLabelView(labelView, video)
            itemView.setOnClickListener { toPlay(it.context, video) }
        }
    }

    companion object {
        private fun toPlay(context: Context, video: VideoBrief) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.KEY_VIDEO_ID, video.id)
            }
            context.startActivity(intent)
        }
    }
}