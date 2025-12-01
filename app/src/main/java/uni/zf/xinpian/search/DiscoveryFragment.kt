package uni.zf.xinpian.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.FragmentDiscoveryBinding
import uni.zf.xinpian.search.SearchType.AREAS
import uni.zf.xinpian.search.SearchType.CATEGORIES
import uni.zf.xinpian.search.SearchType.GENRES
import uni.zf.xinpian.search.SearchType.SORTS
import uni.zf.xinpian.search.SearchType.YEARS
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.video.EvenVideoListAdapter
import uni.zf.xinpian.video.VideoViewModel

class DiscoveryFragment : Fragment(), SearchParamListener {
    private val categories = arrayOf("M", "S", "V", "A", "D")
    private var categoryIndex: Int = 0
    private var area: Int = 0
    private var genre: Int = 0
    private var year: Int = 0
    private var orderBy = ORDER_BY[0]

    private val viewModel: VideoViewModel by viewModels()
    private lateinit var genreAdapter: SearchParamAdapter
    private val videosAdapter = EvenVideoListAdapter()
    private lateinit var binding: FragmentDiscoveryBinding
    private var isDataLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        setupSearchView()
        setupRecyclerViews()
        initVideosView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(object : DefaultLifecycleObserver{
            override fun onResume(owner: LifecycleOwner) {
                if (!isDataLoaded && isVisible) {
                    loadData()
                    isDataLoaded = true
                }
            }
        })
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.updateQueryParams(where(), orderBy)
            viewModel.dataFlow.collectLatest { videosAdapter.submitData(it) }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    private fun setupRecyclerViews() {
        binding.categoryView.setupRecyclerView(CATEGORIES)
        binding.areaView.setupRecyclerView(AREAS)
        binding.yearView.setupRecyclerView(YEARS)
        binding.genreView.setupRecyclerView(GENRES).also { genreAdapter = it }
        binding.sortView.setupRecyclerView(SORTS)
    }

    private fun RecyclerView.setupRecyclerView(type: SearchType): SearchParamAdapter {
        val adapter = SearchParamAdapter(null, type, 0, this@DiscoveryFragment)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter
        return adapter
    }

    private fun initVideosView() {
        binding.videosView.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
            this.adapter = videosAdapter

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

            binding.main.setOnRefreshListener {
                videosAdapter.refresh()
            }
        }
    }

    private fun where(): String {
        val conditions = mutableListOf<String>()
        conditions.add("category == '${categories[categoryIndex]}'")
        if (area > 0) conditions.add("'${AREAS.values[area]}' in areas")
        if (genre > 0) conditions.add("'${GENRES.values(categories[categoryIndex])[genre]}' in genres")

        val yearCondition = when (year) {
            7 -> "year >= 2010 && year < 2020"
            8 -> "year >= 2000 && year < 2010"
            9 -> "year >= 1990 && year < 2000"
            10 -> "year >= 1980 && year < 1990"
            11 -> "year >= 1970 && year < 1980"
            12 -> "year >= 1960 && year < 1970"
            13 -> "year < 1960"
            else -> if (year > 0) "year == ${YEARS.values[year]}" else ""
        }
        if (yearCondition.isNotEmpty()) conditions.add(yearCondition)

        return conditions.joinToString(" && ")
    }

    override fun onSearchParam(type: SearchType, current: Int) {
        when (type) {
            CATEGORIES -> {
                categoryIndex = current
                genre = 0
                genreAdapter.setCategory(categories[categoryIndex])
            }

            AREAS -> area = current
            YEARS -> year = current
            GENRES -> genre = current
            SORTS -> orderBy = ORDER_BY[current]
        }
        viewModel.updateQueryParams(where(), orderBy)
    }
}