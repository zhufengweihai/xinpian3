package uni.zf.xinpian.play.download

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R

data class DownloadTask(
    val id: Long,
    val title: String,
    val status: Int,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val localUri: String?
)

class DownloadListAdapter : RecyclerView.Adapter<DownloadListAdapter.ViewHolder>() {

    private var tasks: List<DownloadTask> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateTasks(newTasks: List<DownloadTask>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download_task, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleView.text = task.title
        holder.statusView.text = getStatusText(task.status)
        holder.sizeView.text = formatSize(task.downloadedBytes, task.totalBytes)

        val progress = if (task.totalBytes > 0) {
            (task.downloadedBytes * 100 / task.totalBytes).toInt()
        } else 0
        holder.progressBar.progress = progress
        holder.progressBar.visibility = if (task.status == android.app.DownloadManager.STATUS_RUNNING) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = tasks.size

    private fun getStatusText(status: Int): String {
        return when (status) {
            android.app.DownloadManager.STATUS_PENDING -> "等待中"
            android.app.DownloadManager.STATUS_RUNNING -> "下载中"
            android.app.DownloadManager.STATUS_PAUSED -> "已暂停"
            android.app.DownloadManager.STATUS_SUCCESSFUL -> "已完成"
            android.app.DownloadManager.STATUS_FAILED -> "下载失败"
            else -> "未知"
        }
    }

    private fun formatSize(downloaded: Long, total: Long): String {
        if (total <= 0) return ""
        val dl = formatBytes(downloaded)
        val tl = formatBytes(total)
        return "$dl / $tl"
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.tv_title)
        val statusView: TextView = view.findViewById(R.id.tv_status)
        val sizeView: TextView = view.findViewById(R.id.tv_size)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
    }
}
