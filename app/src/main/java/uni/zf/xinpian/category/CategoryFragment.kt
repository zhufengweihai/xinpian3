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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
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
    private var dataCollectJob: Job? = null

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
        collectDataOnce()
    }

    private fun loadData() {
        showLoadingIndicator()
        viewModel.requestSlideData()
        viewModel.requestCustomTags()
        viewModel.requestDyTag()
        collectDataOnce()
    }

    private fun collectDataOnce() {
        // 取消之前的收集任务，避免重复收集
        dataCollectJob?.cancel()
        dataCollectJob = lifecycleScope.launch {
            var pendingDataLoads = 3

            // 使用 first { 条件 } 等待有效数据到达后立即停止收集
            // 避免 DataStore 持续发射导致 adapter 反复更新和滚动位置重置
            launch {
                val slideList = viewModel.getSlideList().first { it.data.isNotEmpty() }
                categoryAdapter.updateSlideData(slideList.data)
                pendingDataLoads--
                if (pendingDataLoads == 0) hideRefreshIndicator()
            }

            launch {
                val customTagList = viewModel.getCustomTagList().first { it.list.isNotEmpty() }
                categoryAdapter.updateCustomTags(customTagList.list)
                pendingDataLoads--
                if (pendingDataLoads == 0) hideRefreshIndicator()
            }

            launch {
                val dyTagList = viewModel.getDyTagList().first { it.list.isNotEmpty() }
                categoryAdapter.updateDyTags(dyTagList.list)
                pendingDataLoads--
                if (pendingDataLoads == 0) hideRefreshIndicator()
            }
        }
    }
}
