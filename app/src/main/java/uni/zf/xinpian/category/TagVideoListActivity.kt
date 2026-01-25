package uni.zf.xinpian.category

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ActivityTagVideoListBinding
import uni.zf.xinpian.list.EvenVideoListAdapter
import uni.zf.xinpian.series.SeriesItemDecoration

class TagVideoListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagVideoListBinding
    private val viewModel: TagVideoListViewModel by viewModels()
    private lateinit var adapter: EvenVideoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTagVideoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.tvTitle.text = viewModel.title
        adapter = EvenVideoListAdapter()
        binding.rvTagData.adapter = adapter
        binding.rvTagData.layoutManager = GridLayoutManager(this, 3)
        binding.rvTagData.addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))

        loadData()
    }

    private fun loadData (){
        lifecycleScope.launch {
            viewModel.videoDataFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}