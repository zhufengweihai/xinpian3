package uni.zf.xinpian.download

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.utils.formatBytes
import uni.zf.xinpian.utils.getSourceName

@OptIn(UnstableApi::class)
class DownloadItemAdapter(private val video: DownloadVideo, val callBack: CallBack) :
    Adapter<DownloadItemAdapter.ItemViewHolder>() {
    private var items: List<DownloadItem> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setDownloadItems(items: List<DownloadItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items[holder.bindingAdapterPosition].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)
        private val titleView: TextView = itemView.findViewById(R.id.title_view)
        private val actionView: ImageView = itemView.findViewById(R.id.action_view)
        private val sizeView: TextView = itemView.findViewById(R.id.size_view)
        private val deleteView: ImageView = itemView.findViewById(R.id.delete_view)
        private val progressView: ProgressBar = itemView.findViewById(R.id.progress_view)
        private val statusView: TextView = itemView.findViewById(R.id.status_view)
        private val sourceView: TextView = itemView.findViewById(R.id.source_view)

        init {
            imageView.clipToOutline = true
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: DownloadItem) {
            Glide.with(imageView).load(video.image).into(imageView)
            titleView.text = "${video.name} ${item.episode}"
            bindActionView(item)
            sizeView.text = formatBytes(item.bytesDownloaded)
            deleteView.setOnClickListener { callBack.deleteDownloadItem(item) }
            progressView.progress = if (item.percentDownloaded >= 0) item.percentDownloaded.toInt() else 0
            statusView.text = getStatusText(item)
            sourceView.text = getSourceName(item.vodId)
            itemView.setOnClickListener { callBack.toPlay(item) }
        }

        private fun bindActionView(item: DownloadItem) {
            actionView.setImageResource(getActionDrawable(item))
            actionView.setOnClickListener {
                when (item.state) {
                    Download.STATE_DOWNLOADING -> callBack.pauseDownloadItem(item)
                    Download.STATE_STOPPED -> callBack.resumeDownloadItem(item)
                    Download.STATE_FAILED -> callBack.resumeDownloadItem(item)
                }
            }
        }

        private fun getActionDrawable(item: DownloadItem): Int {
            return when (item.state) {
                Download.STATE_COMPLETED -> R.drawable.ic_complete
                Download.STATE_DOWNLOADING -> R.drawable.ic_start
                else -> R.drawable.ic_pause
            }
        }

        private fun getStatusText(item: DownloadItem): String = when (item.state) {
            Download.STATE_QUEUED -> "等待中"
            Download.STATE_STOPPED -> "已暂停"
            Download.STATE_DOWNLOADING -> "下载中"
            Download.STATE_COMPLETED -> "已完成"
            Download.STATE_FAILED -> "下载失败"
            Download.STATE_REMOVING -> "正在删除"
            Download.STATE_RESTARTING -> "正在重启"
            else -> ""
        }
    }


    interface CallBack {
        fun deleteDownloadItem(item: DownloadItem)
        fun pauseDownloadItem(item: DownloadItem)
        fun resumeDownloadItem(item: DownloadItem)
        fun toPlay(item: DownloadItem)
    }
}