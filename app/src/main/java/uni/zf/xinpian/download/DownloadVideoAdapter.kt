package uni.zf.xinpian.download

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.utils.formatBytes
import uni.zf.xinpian.utils.loadImages

class DownloadVideoAdapter(val callBack: CallBack) : RecyclerView.Adapter<DownloadVideoAdapter.VideoViewHolder>() {
    private var downloadList: List<DownloadVideo> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setDownloadList(downloadList: List<DownloadVideo>) {
        this.downloadList = downloadList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(downloadList[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int = downloadList.size

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)
        private val titleView: TextView = itemView.findViewById(R.id.title_view)
        private val actionView: ImageView = itemView.findViewById(R.id.action_view)
        private val downloadingView: TextView = itemView.findViewById(R.id.downloading_view)
        private val downloadedView: TextView = itemView.findViewById(R.id.downloaded_view)
        private val countView: TextView = itemView.findViewById(R.id.count_view)
        private val sizeView: TextView = itemView.findViewById(R.id.size_view)
        private val deleteView: ImageView = itemView.findViewById(R.id.delete_view)

        init {
            imageView.clipToOutline = true
        }

        fun bind(video: DownloadVideo) {
            Glide.with(imageView).load(video.image).into(imageView)
            titleView.text = video.name
            bindActionView(video)
            downloadingView.text = itemView.resources.getString(R.string.downloading, video.downloading)
            downloadedView.text = itemView.resources.getString(R.string.downloaded, video.downloaded)
            countView.text = itemView.resources.getString(R.string.download_count, video.count)
            sizeView.text = formatBytes(video.totalSize)
            deleteView.setOnClickListener { callBack.deleteItemsByVideoId(video.videoId) }
            itemView.setOnClickListener { callBack.viewItemsByVideoId(video) }
        }

        private fun bindActionView(video: DownloadVideo) {
            actionView.setImageResource(getActionDrawable(video))
            actionView.setOnClickListener {
                if (video.downloading > 0) {
                    callBack.pauseItemsByVideoId(video.videoId)
                } else if (video.downloaded < video.count) {
                    callBack.resumeItemsByVideoId(video.videoId)
                }
            }
        }

        private fun getActionDrawable(video: DownloadVideo): Int {
            if (video.downloaded == video.count) {
                return R.drawable.ic_complete
            } else if (video.downloading > 0) {
                return R.drawable.ic_start
            } else {
                return R.drawable.ic_pause
            }
        }
    }

    interface CallBack {
        fun deleteItemsByVideoId(videoId: String)
        fun pauseItemsByVideoId(videoId: String)
        fun resumeItemsByVideoId(videoId: String)
        fun viewItemsByVideoId(video: DownloadVideo)
    }
}