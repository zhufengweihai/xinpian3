package uni.zf.xinpian.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.databinding.FragmentCategoryBinding

fun newCategoryFragment(categoryId: Int) = CategoryFragment().apply {
    arguments = Bundle().apply { putInt(ARG_CATEGORY, categoryId) }
}

class CategoryFragment : Fragment() {
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var hasLoaded = false
    private var isRefreshing = false

    // 下拉刷新相关
    private var touchStartY = 0f
    private var isPulling = false
    private val refreshThreshold = 160f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        if (!hasLoaded) {
            loadData()
            hasLoaded = true
        }
    }

    private fun init() {
        categoryAdapter = CategoryAdapter()
        categoryAdapter.setHasStableIds(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = categoryAdapter
        binding.recyclerView.setItemViewCacheSize(10)
        binding.recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        setupPullToRefresh()
    }

    private fun setupPullToRefresh() {
        binding.recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (isRefreshing) return false
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchStartY = e.y
                        isPulling = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaY = e.y - touchStartY
                        if (deltaY > 30 && !rv.canScrollVertically(-1)) {
                            isPulling = true
                            return true
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val deltaY = e.y - touchStartY
                        val pullDistance = (deltaY * 0.4f).coerceAtLeast(0f)
                        showPullProgress(pullDistance)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        val deltaY = e.y - touchStartY
                        if (deltaY >= refreshThreshold) {
                            triggerRefresh()
                        } else {
                            hidePullProgress()
                        }
                        isPulling = false
                    }
                }
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun showPullProgress(pullDistance: Float) {
        binding.refreshProgress.visibility = View.VISIBLE
        binding.refreshProgress.translationY = pullDistance - 20f
        val progress = (pullDistance / (refreshThreshold * 0.4f)).coerceIn(0f, 1f)
        binding.refreshProgress.alpha = progress
        binding.refreshProgress.scaleX = progress.coerceIn(0.4f, 1f)
        binding.refreshProgress.scaleY = progress.coerceIn(0.4f, 1f)
    }

    private fun hidePullProgress() {
        binding.refreshProgress.animate()
            .alpha(0f)
            .translationY(-40f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                binding.refreshProgress.visibility = View.GONE
            }
            .start()
    }

    private fun triggerRefresh() {
        isRefreshing = true
        binding.refreshProgress.animate()
            .translationY(10f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(150)
            .setInterpolator(DecelerateInterpolator())
            .start()
        refreshData()
    }

    private fun showLoadingIndicator() {
        isRefreshing = true
        binding.refreshProgress.visibility = View.VISIBLE
        binding.refreshProgress.translationY = 10f
        binding.refreshProgress.alpha = 1f
        binding.refreshProgress.scaleX = 1f
        binding.refreshProgress.scaleY = 1f
    }

    private fun hideRefreshIndicator() {
        binding.refreshProgress.animate()
            .alpha(0f)
            .translationY(-40f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                binding.refreshProgress.visibility = View.GONE
                isRefreshing = false
            }
            .start()
    }

    private fun refreshData() {
        viewModel.requestSlideData()
        viewModel.requestCustomTags()
        viewModel.requestDyTag()
        collectDataWithRefresh()
    }

    private fun loadData() {
        showLoadingIndicator()
        viewModel.requestSlideData()
        viewModel.requestCustomTags()
        viewModel.requestDyTag()
        collectDataWithLoading()
    }

    private fun collectDataWithLoading() {
        var pendingDataLoads = 3

        lifecycleScope.launch {
            viewModel.getSlideList().collect {
                if (it.data.isNotEmpty()) categoryAdapter.updateSlideData(it.data)
                pendingDataLoads--
                if (pendingDataLoads == 0) hideRefreshIndicator()
            }
        }

        lifecycleScope.launch {
            viewModel.getCustomTagList().collect {
                if (it.list.isNotEmpty()) categoryAdapter.updateCustomTags(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0) hideRefreshIndicator()
            }
        }

        lifecycleScope.launch {
            viewModel.getDyTagList().collect {
                if (it.list.isNotEmpty()) categoryAdapter.updateDyTags(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0) hideRefreshIndicator()
            }
        }
    }

    private fun collectDataWithRefresh() {
        var pendingDataLoads = 3
        var refreshComplete = false

        lifecycleScope.launch {
            viewModel.getSlideList().collect {
                categoryAdapter.updateSlideData(it.data)
                pendingDataLoads--
                if (pendingDataLoads == 0 && !refreshComplete) {
                    refreshComplete = true
                    hideRefreshIndicator()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getCustomTagList().collect {
                if (it.list.isNotEmpty()) categoryAdapter.updateCustomTags(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0 && !refreshComplete) {
                    refreshComplete = true
                    hideRefreshIndicator()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getDyTagList().collect {
                categoryAdapter.updateDyTags(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0 && !refreshComplete) {
                    refreshComplete = true
                    hideRefreshIndicator()
                }
            }
        }
    }
}
