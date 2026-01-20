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
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.FragmentListBinding
import uni.zf.xinpian.search.ORDER_BY
import uni.zf.xinpian.search.SearchActivity
import uni.zf.xinpian.search.SearchParamListener
import uni.zf.xinpian.search.SearchType
import uni.zf.xinpian.search.SearchType.AREAS
import uni.zf.xinpian.search.SearchType.CATEGORIES
import uni.zf.xinpian.search.SearchType.GENRES
import uni.zf.xinpian.search.SearchType.SORTS
import uni.zf.xinpian.search.SearchType.YEARS
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.video.EvenVideoListAdapter

class ListFragment : Fragment(), SearchParamListener {
    private var categoryIndex: Int = 0
    private var area: Int = 0
    private var genre: Int = 0
    private var year: Int = 0
    private var orderBy = ORDER_BY[0]

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
            viewModel.getFilterGroups().forEach {
                (binding.typeView.adapter as SearchParamAdapter).setOptions(it.options)
                (binding.areaView.adapter as SearchParamAdapter).setOptions(it.options)
                (binding.yearView.adapter as SearchParamAdapter).setOptions(it.options)
                (binding.sortView.adapter as SearchParamAdapter).setOptions(it.options)
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

    private fun RecyclerView.setupRecyclerView(): SearchParamAdapter {
        val adapter = SearchParamAdapter(selectedPosition = 0, listener = this@ListFragment)
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

    override fun onSearchParam(type: SearchType, current: Int) {
        when (type) {
            CATEGORIES -> {
                categoryIndex = current
                genre = 0
            }

            AREAS -> area = current
            YEARS -> year = current
            GENRES -> genre = current
            SORTS -> orderBy = ORDER_BY[current]
        }
    }
}