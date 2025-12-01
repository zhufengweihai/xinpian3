package uni.zf.xinpian.download

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.databinding.ActivityDownloadItemBinding
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.view.DividerItemDecoration
import uni.zf.xinpian.view.showConfirmationDialog

class DownloadItemActivity : AppCompatActivity(), DownloadItemAdapter.CallBack {
    private val binding by lazy { ActivityDownloadItemBinding.inflate(layoutInflater) }
    private val viewModel: DownloadViewModel by viewModels()
    private lateinit var video: DownloadVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        video = savedInstanceState?.getSerializable(EXTRA_VIDEO) as? DownloadVideo
            ?: intent.getSerializableExtra(EXTRA_VIDEO) as? DownloadVideo ?: return
        initDownloadItemView()
        initButtonView()
        lifecycle.addObserver(DownloadObserver(1000))
    }

    private fun initDownloadItemView() {
        val adapter = DownloadItemAdapter(video, this)
        binding.downloadItemView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(color = ContextCompat.getColor(context, R.color.divider)))
        }
        lifecycleScope.launch {
            viewModel.getAllItems(video.videoId).collect { adapter.setDownloadItems(it) }
        }
    }

    private fun initButtonView() {
        binding.deleteAllView.setOnClickListener {
            showConfirmationDialog("确定要全部删除吗？") {
                lifecycleScope.launch { viewModel.deleteAllItemsByVideoId(video.videoId) }
            }
        }
        binding.pauseAllView.setOnClickListener { lifecycleScope.launch { viewModel.pauseDownloads(video.videoId) } }
        binding.startAllView.setOnClickListener { lifecycleScope.launch { viewModel.resumeDownloads(video.videoId) } }
    }

    companion object {
        const val EXTRA_VIDEO = "video"
    }

    override fun deleteDownloadItem(item: DownloadItem) {
        viewModel.deleteItem(item.url)
    }

    override fun pauseDownloadItem(item: DownloadItem) {
        viewModel.pauseDownloadItem(item.url)
    }

    override fun resumeDownloadItem(item: DownloadItem) {
        viewModel.resumeDownload(item.url)
    }

    override fun toPlay(item: DownloadItem) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.KEY_VIDEO_ID, video.videoId)
        }
        startActivity(intent)
    }
}