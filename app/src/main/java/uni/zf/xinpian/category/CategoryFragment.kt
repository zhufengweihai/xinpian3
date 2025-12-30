package uni.zf.xinpian.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.series.SeriesItemDecoration

fun newCategoryFragment(categoryId: Int) = CategoryFragment().apply {
    arguments = Bundle().apply { putInt(ARG_CATEGORY, categoryId) }
}

class CategoryFragment() : Fragment() {
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var dyTagAdapter : DyTagListAdapter
    private lateinit var cumTagAdapter : CustomTagAdapter
    private var isDataLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isDataLoaded && savedInstanceState == null) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun init(){
        dyTagAdapter = DyTagListAdapter()
        binding.rvDyTagList.adapter = dyTagAdapter
        cumTagAdapter = CustomTagAdapter()
        binding.tagListView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.tagListView.addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
        binding.tagListView.adapter = cumTagAdapter
    }
    private fun loadData() {
        viewModel.requestSlideData()
        viewModel.requestCustomTags()
        viewModel.requestDyTag()
        lifecycleScope.launch {
            viewModel.getSlideList().collect {
                binding.slideView.setVideoList(it.data, true)
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