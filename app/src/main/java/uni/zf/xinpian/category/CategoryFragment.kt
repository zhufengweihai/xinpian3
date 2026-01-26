package uni.zf.xinpian.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.view.HorizontalItemDecoration

fun newCategoryFragment(categoryId: Int) = CategoryFragment().apply {
    arguments = Bundle().apply { putInt(ARG_CATEGORY, categoryId) }
}

class CategoryFragment() : Fragment() {
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var dyTagAdapter: DyTagListAdapter
    private lateinit var cumTagAdapter: CustomTagAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        dyTagAdapter = DyTagListAdapter()
        binding.rvDyTagList.adapter = dyTagAdapter
        cumTagAdapter = CustomTagAdapter()
        binding.tagListView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.tagListView.addItemDecoration(HorizontalItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
        binding.tagListView.adapter = cumTagAdapter
        loadData()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        binding.swipeRefreshLayout.isRefreshing = true
        loadCommonData(isRefresh = true)
    }

    private fun loadData() {
        loadCommonData(isRefresh = false)
    }

    private fun loadCommonData(isRefresh: Boolean = false) {
        viewModel.requestSlideData()
        viewModel.requestCustomTags()
        viewModel.requestDyTag()
        if (isRefresh) collectDataWithRefresh() else collectDataWithoutRefresh()
    }

    private fun collectDataWithRefresh() {
        var pendingDataLoads = 3 // 跟踪待加载的数据项数量（slide、custom tags、dy tags）

        lifecycleScope.launch {
            viewModel.getSlideList().collect {
                binding.slideView.setVideoList(it.data)
                pendingDataLoads--
                if (pendingDataLoads == 0) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getCustomTagList().collect {
                if (it.list.isNotEmpty()) cumTagAdapter.updateCustomTagList(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getDyTagList().collect {
                dyTagAdapter.updateDyTagList(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun collectDataWithoutRefresh() {
        lifecycleScope.launch {
            viewModel.getSlideList().collect {
                binding.slideView.setVideoList(it.data)
            }
        }

        lifecycleScope.launch {
            viewModel.getCustomTagList().collect {
                if (it.list.isNotEmpty()) cumTagAdapter.updateCustomTagList(it.list)
            }
        }

        lifecycleScope.launch {
            viewModel.getDyTagList().collect {
                dyTagAdapter.updateDyTagList(it.list)
            }
        }
    }
}