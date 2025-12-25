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
import uni.zf.xinpian.data.model.Category
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.view.TagDataView

const val arg_category = "category"

fun newCategoryFragment(category: Category) = CategoryFragment().apply {
    arguments = Bundle().apply { putParcelable(arg_category, category) }
}

class CategoryFragment() : Fragment() {
    private val category: Category? by lazy { arguments?.getParcelable(arg_category) }
    private val viewModel: CategoryViewModel by viewModels()

    private lateinit var binding: FragmentCategoryBinding
    private var isDataLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isDataLoaded && savedInstanceState == null) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        if (category == null) return
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
                if (it.data.isNotEmpty()) setupCustomTagsView(it.data)
            }
        }
        lifecycleScope.launch {
            viewModel.getDyTag().collect {
                for (tagData in it.dataList){
                    val tagDataView = TagDataView(requireContext())
                    tagDataView.setTagData(tagData)
                    binding.main.addView(tagDataView)
                }
            }
        }
    }

    private fun setupCustomTagsView(customTags:List<CustomTag>) {
        val adapter = TagAdapter(customTags)
        binding.tagListView.apply {
            visibility = View.VISIBLE
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
            this.adapter = adapter
        }
    }
}