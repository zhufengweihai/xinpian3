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
import uni.zf.xinpian.data.model.CustomTag
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.view.TagDataView

class CategoryFragment() : Fragment() {
    private val category: Category? by lazy { arguments?.getParcelable(ARG_FENLEI) }
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
        lifecycleScope.launch {
            viewModel.getSlideDataList(category!!.id).collect {
                binding.slideView.setVideoList(it, true)
            }
            viewModel.getTagList(category!!.id).collect {
                if (it.isNotEmpty()) setupCustomTagsView(it)
            }
            viewModel.getTagDatas(category!!.id).collect {
                for (tagData in it){
                    val tagDataView = TagDataView(requireContext())
                    tagDataView.setTagData(tagData)
                    binding.main.addView(tagDataView)
                }
            }

            viewModel.requestSlideData(category!!.id,requireContext())
            viewModel.requestCustomTags(category!!.id,requireContext())
            viewModel.requestTagDatas(category!!.id,requireContext())
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

    companion object {
        private const val ARG_FENLEI = "fenlei"

        @JvmStatic
        fun newInstance(category: Category): CategoryFragment {
            return CategoryFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_FENLEI, category) }
            }
        }
    }
}