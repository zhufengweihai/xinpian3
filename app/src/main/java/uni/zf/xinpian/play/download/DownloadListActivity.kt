package uni.zf.xinpian.play.download

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import uni.zf.xinpian.databinding.ActivityDownloadListBinding

class DownloadListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadListBinding
    private lateinit var adapter: DownloadListAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadDownloads()
            handler.postDelayed(this, 2000) // 每2秒刷新一次进度
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ivBack.setOnClickListener { finish() }

        adapter = DownloadListAdapter()
        binding.rvDownloads.adapter = adapter

        loadDownloads()
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun loadDownloads() {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        val cursor: Cursor? = downloadManager.query(query)

        val tasks = mutableListOf<DownloadTask>()

        cursor?.use {
            val idIndex = it.getColumnIndex(DownloadManager.COLUMN_ID)
            val titleIndex = it.getColumnIndex(DownloadManager.COLUMN_TITLE)
            val statusIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val totalIndex = it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val downloadedIndex = it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val localUriIndex = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)

            while (it.moveToNext()) {
                val title = if (titleIndex >= 0) it.getString(titleIndex) ?: "" else ""
                // 只显示本 App 的下载（标题包含视频名）
                tasks.add(
                    DownloadTask(
                        id = if (idIndex >= 0) it.getLong(idIndex) else 0,
                        title = title,
                        status = if (statusIndex >= 0) it.getInt(statusIndex) else 0,
                        totalBytes = if (totalIndex >= 0) it.getLong(totalIndex) else 0,
                        downloadedBytes = if (downloadedIndex >= 0) it.getLong(downloadedIndex) else 0,
                        localUri = if (localUriIndex >= 0) it.getString(localUriIndex) else null
                    )
                )
            }
        }

        // 按状态排序：下载中 > 等待中 > 已暂停 > 已完成 > 失败
        tasks.sortWith(compareBy {
            when (it.status) {
                DownloadManager.STATUS_RUNNING -> 0
                DownloadManager.STATUS_PENDING -> 1
                DownloadManager.STATUS_PAUSED -> 2
                DownloadManager.STATUS_SUCCESSFUL -> 3
                else -> 4
            }
        })

        adapter.updateTasks(tasks)
        binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        binding.rvDownloads.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
    }
}
