package uni.zf.xinpian.play.download

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import uni.zf.xinpian.databinding.ActivityDownloadSelectBinding
import uni.zf.xinpian.json.model.SourceGroup

class DownloadSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadSelectBinding
    private lateinit var sourceAdapter: SourceSelectAdapter
    private lateinit var episodeAdapter: EpisodeSelectAdapter
    private var sourceGroups: List<SourceGroup> = emptyList()
    private var videoTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        videoTitle = DownloadDataHolder.title
        sourceGroups = DownloadDataHolder.sourceGroups

        if (sourceGroups.isEmpty()) {
            Toast.makeText(this, "无可用片源", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
    }

    private fun initViews() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btnDownloadList.setOnClickListener {
            startActivity(Intent(this, DownloadListActivity::class.java))
        }

        // 片源列表
        sourceAdapter = SourceSelectAdapter { position ->
            episodeAdapter.updateItems(sourceGroups[position].playList)
        }
        binding.rvSources.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSources.adapter = sourceAdapter
        sourceAdapter.updateSources(sourceGroups)

        // 选集列表
        episodeAdapter = EpisodeSelectAdapter()
        binding.rvEpisodes.adapter = episodeAdapter
        episodeAdapter.updateItems(sourceGroups[0].playList)

        // 全选
        binding.btnSelectAll.setOnClickListener {
            episodeAdapter.selectAll()
        }

        // 开始下载
        binding.btnStartDownload.setOnClickListener {
            startDownload()
        }
    }

    private fun startDownload() {
        val selectedItems = episodeAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "请选择要下载的集数", Toast.LENGTH_SHORT).show()
            return
        }

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var count = 0

        selectedItems.forEach { item ->
            try {
                val request = DownloadManager.Request(Uri.parse(item.url)).apply {
                    setTitle("$videoTitle - ${item.sourceName}")
                    setDescription("正在下载...")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_MOVIES,
                        "xinpian/${videoTitle}/${item.sourceName}.mp4"
                    )
                    setAllowedOverMetered(true)
                    setAllowedOverRoaming(false)
                }
                downloadManager.enqueue(request)
                count++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (count > 0) {
            Toast.makeText(this, "已添加 $count 个下载任务", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show()
        }
    }
}
