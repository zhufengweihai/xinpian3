package uni.zf.xinpian.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.KEY_VIDEO_ID
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.play.PlayActivity
import uni.zf.xinpian.utils.ImageLoadUtil

class SlideImageAdapter(private val videoList: List<SlideData>) : Adapter<SlideImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount() = videoList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.banner_image_view)
        fun bind(slideData: SlideData) {
            ImageLoadUtil.loadImages(imageView, slideData.thumbnail)
            itemView.setOnClickListener { toPlay(itemView.context, slideData) }
        }

        private fun toPlay(context: Context, slideData: SlideData) {
            val intent = Intent(context, PlayActivity::class.java).apply {
                putExtra(KEY_VIDEO_ID, slideData.jumpId)
            }
            context.startActivity(intent)
        }
    }
}