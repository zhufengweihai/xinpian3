package uni.zf.xinpian.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.databinding.ActivityWatchHistoryBinding
import uni.zf.xinpian.player.PlayerActivity
import uni.zf.xinpian.view.DividerItemDecoration
import uni.zf.xinpian.view.showConfirmationDialog

class WatchHistoryActivity : AppCompatActivity(), WatchHistoryAdapter.CallBack {
    private lateinit var binding: ActivityWatchHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWatchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        initDeleteAllView()
    }

    private fun initRecyclerView() {
        val adapter = WatchHistoryAdapter(this)
        binding.watchListView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(color = ContextCompat.getColor(context, R.color.divider)))
        }
        lifecycleScope.launch {

        }
    }

    private fun initDeleteAllView() {
        binding.deleteAllView.setOnClickListener {
            showConfirmationDialog("确定要全部删除吗？") {
                lifecycleScope.launch {

                }
            }
        }
    }

    override fun deleteRecord(watchRecord: WatchRecord) {

    }

    override fun toPlay(watchRecord: WatchRecord) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.KEY_VIDEO_ID, watchRecord.videoId)
        })
    }
}