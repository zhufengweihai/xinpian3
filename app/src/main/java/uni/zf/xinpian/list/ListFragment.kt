package uni.zf.xinpian.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.FragmentListBinding
import uni.zf.xinpian.json.model.FilterOption
import uni.zf.xinpian.search.SearchActivity
import uni.zf.xinpian.series.SeriesItemDecoration

class ListFragment : Fragment(), FilterOptionListener {
    private val filterOptions = FilterOptions()
    private val viewModel: ListViewModel by viewModels()
    private val videosAdapter = EvenVideoListAdapter()
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        setupSearchView()
        setupRecyclerViews()
        loadData()
        initVideosView()
        return binding.root
    }

    private fun loadData() {
        lifecycleScope.launch {
            val filterGroups = viewModel.getFilterGroups()
            (binding.typeView.adapter as FilterOptionAdapter).setOptions(filterGroups[0])
            (binding.areaView.adapter as FilterOptionAdapter).setOptions(filterGroups[1])
            (binding.yearView.adapter as FilterOptionAdapter).setOptions(filterGroups[2])
            (binding.sortView.adapter as FilterOptionAdapter).setOptions(filterGroups[3])
        }
        lifecycleScope.launch {
            viewModel.videoFlow.collectLatest {
                videosAdapter.submitData(it)
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    private fun setupRecyclerViews() {
        binding.typeView.setupRecyclerView()
        binding.areaView.setupRecyclerView()
        binding.yearView.setupRecyclerView()
        binding.sortView.setupRecyclerView()
    }

    private fun RecyclerView.setupRecyclerView(): FilterOptionAdapter {
        val adapter = FilterOptionAdapter(listener = this@ListFragment)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter
        return adapter
    }

    private fun initVideosView() {
        binding.videosView.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
            this.adapter = videosAdapter
            listenLoadState()
            binding.main.setOnRefreshListener(videosAdapter::refresh)
        }
    }

    private fun listenLoadState() {
        videosAdapter.addLoadStateListener {
            when {
                it.refresh is Loading || it.append is Loading -> binding.main.isRefreshing = true
                it.refresh is Error || it.append is Error -> {
                    makeText(context, "请求失败", LENGTH_SHORT).show()
                    binding.main.isRefreshing = false
                }

                else -> binding.main.isRefreshing = false
            }
        }
    }

    override fun onFilterOption(key:String, option: FilterOption) {
        when (key) {
            "type" -> filterOptions.type = option.id
            "area" -> filterOptions.area = option.id
            "year" -> filterOptions.year = option.id
            "sort" -> filterOptions.sort = option.id
        }
        viewModel.updateFilterOptions(filterOptions)
    }
}