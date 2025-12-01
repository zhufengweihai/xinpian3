package uni.zf.xinpian.download

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.databinding.FragmentDownloadBinding
import uni.zf.xinpian.utils.formatSizeToGB
import uni.zf.xinpian.view.DividerItemDecoration
import uni.zf.xinpian.view.showConfirmationDialog

@UnstableApi
class DownloadFragment : Fragment(), DownloadVideoAdapter.CallBack {
    private val viewModel: DownloadViewModel by viewModels()
    private var binding: FragmentDownloadBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)
        initDownloadListView()
        initButtonView()
        initCleanView()
        lifecycle.addObserver(DownloadObserver())
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        initMemInfoView()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun initDownloadListView() {
        val adapter = DownloadVideoAdapter(this)
        binding!!.downloadListView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(color = ContextCompat.getColor(context, R.color.divider)))
        }
        lifecycleScope.launch {
            viewModel.getAllVideos().collect {
                adapter.setDownloadList(it)
                binding!!.tipView.isVisible = it.isEmpty()
            }
        }
    }

    private fun initButtonView() {
        binding!!.deleteAllView.setOnClickListener {
            showConfirmationDialog("确定要全部删除吗？") {
                lifecycleScope.launch { viewModel.deleteAll() }
            }
        }
        binding!!.pauseAllView.setOnClickListener { lifecycleScope.launch { viewModel.pauseAllDownloads() } }
        binding!!.startAllView.setOnClickListener { lifecycleScope.launch { viewModel.resumeAllDownloads() } }
    }

    private fun initMemInfoView() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            updateMemoryInfo()
        }
    }

    private fun updateMemoryInfo() {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val usedBlocks = totalBlocks - stat.availableBlocksLong
        val availableMem = stat.availableBlocksLong * blockSize
        binding!!.memInfoBar.progress = (usedBlocks * 100 / totalBlocks).toInt()
        binding!!.memInfoView.text = getString(R.string.available_mem, formatSizeToGB(availableMem))
    }

    private fun initCleanView() {
        binding!!.cleanView.setOnClickListener {
            showConfirmationDialog("确定要清除全部缓存吗？") {
                lifecycleScope.launch {
                    viewModel.deleteAllCache()
                    initMemInfoView()
                }
            }
        }
    }

    override fun deleteItemsByVideoId(videoId: String) {
        showConfirmationDialog("确定要删除吗？") {
            lifecycleScope.launch {
                viewModel.deleteAllItemsByVideoId(videoId)
            }
        }
    }

    override fun pauseItemsByVideoId(videoId: String) {
        lifecycleScope.launch {
            viewModel.pauseDownloads(videoId)
        }
    }

    override fun resumeItemsByVideoId(videoId: String) {
        lifecycleScope.launch {
            viewModel.resumeDownloads(videoId)
        }
    }

    override fun viewItemsByVideoId(video: DownloadVideo) {
        startActivity(Intent(context, DownloadItemActivity::class.java).apply {
            putExtra(DownloadItemActivity.EXTRA_VIDEO, video)
        })
    }
}