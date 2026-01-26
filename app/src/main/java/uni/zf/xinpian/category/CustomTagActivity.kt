package uni.zf.xinpian.category

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ActivityCustomTagBinding
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.utils.ImageLoadUtil.loadImagesWithDomain

class CustomTagActivity : AppCompatActivity() {
    private val viewModel: CustomTagViewModel by viewModels()
    private lateinit var binding: ActivityCustomTagBinding
    private val adapter = VideoListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCustomTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.rvVideoList.adapter = adapter
        binding.rvVideoList.layoutManager = GridLayoutManager(this, 3)
        binding.rvVideoList.addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.getCustomTagData()?.let {
                loadImagesWithDomain(binding.ivCover, it.coverUrl)
                binding.tvTitle.text = it.title
                binding.tvContent.text = it.content
                adapter.updateVideoList(it.videoList)
            }
        }
    }
}