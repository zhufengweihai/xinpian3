package uni.zf.xinpian.category

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.utils.QrCodeScanner
import uni.zf.xinpian.view.HorizontalItemDecoration

fun newCategoryFragment(categoryId: Int) = CategoryFragment().apply {
    arguments = Bundle().apply { putInt(ARG_CATEGORY, categoryId) }
}

class CategoryFragment : Fragment() {
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var dyTagAdapter: DyTagListAdapter
    private lateinit var cumTagAdapter: CustomTagAdapter
    private var hasLoaded = false

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
        dyTagAdapter = DyTagListAdapter()
        binding.rvDyTagList.adapter = dyTagAdapter
        cumTagAdapter = CustomTagAdapter()
        binding.tagListView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.tagListView.addItemDecoration(HorizontalItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
        binding.tagListView.adapter = cumTagAdapter
        setupSwipeRefresh()
        setupAdImageClick()
    }

    private fun setupAdImageClick() {
        binding.adImageView.setOnClickListener {
            val bitmap = (binding.adImageView.drawable as? BitmapDrawable)?.bitmap
            if (bitmap == null) {
                Toast.makeText(requireContext(), "图片未加载", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            scanQrCode(bitmap)
        }
    }

    private fun scanQrCode(bitmap: Bitmap) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) {
                QrCodeScanner.scanQrCode(bitmap)
            }

            if (!isAdded) return@launch
            if (result != null) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, result.toUri()))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "无法打开: $result", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "未识别到二维码", Toast.LENGTH_SHORT).show()
            }
        }
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
        var refreshComplete = false

        lifecycleScope.launch {
            viewModel.getSlideList().collect {
                binding.slideView.setVideoList(it.data)
                pendingDataLoads--
                if (pendingDataLoads == 0 && !refreshComplete) {
                    refreshComplete = true
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getCustomTagList().collect {
                if (it.list.isNotEmpty()) cumTagAdapter.updateCustomTagList(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0 && !refreshComplete) {
                    refreshComplete = true
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getDyTagList().collect {
                dyTagAdapter.updateDyTagList(it.list)
                pendingDataLoads--
                if (pendingDataLoads == 0 && !refreshComplete) {
                    refreshComplete = true
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