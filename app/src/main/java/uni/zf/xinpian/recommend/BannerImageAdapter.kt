package uni.zf.xinpian.recommend

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.utils.loadImages

class BannerImageAdapter(private val videoList: List<Video>) : Adapter<BannerImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount() = videoList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.banner_image_view)

        fun bind(video: Video) {
            Glide.with(imageView).load(video.image).into(imageView)
            itemView.setOnClickListener { toPlay(itemView.context, video) }
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