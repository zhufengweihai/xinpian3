package uni.zf.xinpian.record

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.databinding.FragmentWatchRecordBinding
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.view.DividerItemDecoration
import uni.zf.xinpian.view.showConfirmationDialog

class WatchRecordFragment : Fragment(), WatchRecordAdapter.CallBack {

    private val viewModel: WatchRecordViewModel by viewModels()
    private var binding: FragmentWatchRecordBinding? = null
    private var toConfirm = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWatchRecordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initDeleteAllView()
    }

    private fun initRecyclerView() {
        val adapter = WatchRecordAdapter(this)
        binding!!.watchListView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(color = ContextCompat.getColor(context, R.color.divider)))
        }
        lifecycleScope.launch {
            viewModel.watchList.collect { adapter.setWatchList(it) }
        }
    }

    private fun initDeleteAllView() {
        binding!!.deleteAllView.setOnClickListener {
            showConfirmationDialog("确定要全部删除吗？") {
                lifecycleScope.launch {
                    viewModel.deleteAll()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun deleteRecord(watchRecord: WatchRecord) {
        if (!toConfirm) {
            lifecycleScope.launch {
                viewModel.deleteWatchRecord(watchRecord)
                toConfirm = true
            }
            return
        }
        showConfirmationDialog("确定要删除吗？") {
            lifecycleScope.launch {
                viewModel.deleteWatchRecord(watchRecord)
                toConfirm = true
            }
        }
    }

    override fun toPlay(watchRecord: WatchRecord) {
        startActivity(Intent(context, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.KEY_VIDEO_ID, watchRecord.videoId)
        })
    }
}