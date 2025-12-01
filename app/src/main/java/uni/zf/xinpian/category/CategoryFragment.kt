package uni.zf.xinpian.category

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.RecData
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.databinding.FragmentSeriesBinding
import uni.zf.xinpian.main.VideoDataViewModel
import uni.zf.xinpian.search.SearchParamAdapter
import uni.zf.xinpian.search.SearchParamListener
import uni.zf.xinpian.search.SearchType
import uni.zf.xinpian.search.SearchType.GENRES
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.video.EvenVideoListAdapter
import uni.zf.xinpian.video.VideoViewModel

class CategoryFragment : Fragment(), SearchParamListener {

    private val viewModel: VideoViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding
    private var isDataLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        setupGenreView()
        setupSeriesView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (!isDataLoaded && isVisible) {
                    initVideoList(binding.seriesView.adapter as EvenVideoListAdapter)
                    isDataLoaded = true
                }
            }
        })
    }

    private fun setupGenreView() {
        if (category == "D") {
            binding.genreView.visibility = View.GONE
            return
        }
        binding.genreView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = SearchParamAdapter(category, GENRES, 0, this@CategoryFragment)
        }
    }

    private fun setupSeriesView() {
        val adapter = EvenVideoListAdapter()
        binding.seriesView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
            this.adapter = adapter
        }
        initRecData()
        initLoadingView(adapter)
    }

    private fun initLoadingView(adapter: EvenVideoListAdapter) {
        adapter.addLoadStateListener {
            when {
                it.refresh is Loading || it.append is Loading -> binding.swipeRefreshLayout.isRefreshing = true
                it.refresh is Error || it.append is Error -> makeText(context, "请求失败", LENGTH_SHORT).show()
                else -> binding.swipeRefreshLayout.isRefreshing = false
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun initRecData() {
        val videoDataViewModel = ViewModelProvider(requireActivity())[VideoDataViewModel::class.java]
        videoDataViewModel.recData.observe(viewLifecycleOwner) {
            binding.recView.setVideoList(getRecVideos(it), true)
        }
    }

    private fun initVideoList(adapter: EvenVideoListAdapter) {
        viewModel.updateQueryParams(buildQuery(category!!, 0))
        lifecycleScope.launch {
            viewModel.dataFlow.collectLatest { adapter.submitData(it) }
        }
    }

    private fun getRecVideos(recData: RecData): List<Video> {
        return when (category) {
            "M" -> recData.hot_movie_list
            "S" -> recData.hot_tv_list
            "V" -> recData.hot_variety_list
            "A" -> recData.hot_animation_list
            "D" -> recData.hot_duanju_list
            else -> emptyList()
        }.take(3)
    }

    private fun buildQuery(category: String, genreIndex: Int = 0): String {
        return if (genreIndex > 0) {
            "category==\"$category\" && \"${GENRES.values(category)[genreIndex]}\" in genres"
        } else {
            "category==\"$category\""
        }
    }

    companion object {
        private const val ARG_CATEGORY = "category"

        @JvmStatic
        fun newInstance(category: String?): CategoryFragment {
            return CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
        }
    }

    override fun onSearchParam(type: SearchType, current: Int) {
        category?.let { viewModel.updateQueryParams(buildQuery(it, current)) }
    }
}