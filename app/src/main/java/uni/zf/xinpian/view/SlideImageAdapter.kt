package uni.zf.xinpian.view

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
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.model.SlideData
import uni.zf.xinpian.player.PlayerActivity

class SlideImageAdapter(private val videoList: List<SlideData>) : Adapter<SlideImageAdapter.ViewHolder>() {

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
        val imgDomain = AppData.getInstance(itemView.context).imgDomain()
        fun bind(video: SlideData) {
            Glide.with(imageView).load(imgDomain+video.thumbnail).into(imageView)
            itemView.setOnClickListener { toPlay(itemView.context, video) }
        }
    }

    companion object {
        private fun toPlay(context: Context, video: SlideData) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.KEY_VIDEO_ID, video.jumpId)
            }
            context.startActivity(intent)
        }
    }
}