package uni.zf.xinpian.play

import android.annotation.SuppressLint
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
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_VIDEO_ID
import uni.zf.xinpian.data.model.RelatedVideo
import uni.zf.xinpian.utils.ImageLoadUtil

class RelatedVideoAdapter(private var relatedVideoList: List<RelatedVideo> = listOf()) : Adapter<RelatedVideoAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateRelatedVideos(relatedVideoList: List<RelatedVideo>) {
        this.relatedVideoList = relatedVideoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_grid_video, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(relatedVideoList[position])
    }

    override fun getItemCount(): Int = relatedVideoList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_image)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)
        init {
            itemView.clipToOutline = true
        }

        fun bind(relatedVideo: RelatedVideo) {
            if (relatedVideo.imageUrl.startsWith("http")){
                Glide.with(imageView.context).load(relatedVideo.imageUrl).into(imageView)
            }else {
                ImageLoadUtil.loadImages(imageView, relatedVideo.imageUrl)
            }

            nameView.text = relatedVideo.title
            itemView.setOnClickListener { toPlay(it.context, relatedVideo) }
        }
    }

    companion object {
        private fun toPlay(context: Context, relatedVideo: RelatedVideo) {
            val intent = Intent(context, PlayActivity::class.java).apply {
                putExtra(ARG_VIDEO_ID, relatedVideo.videoId)
            }
            context.startActivity(intent)
        }
    }
}