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
import uni.zf.xinpian.App
import uni.zf.xinpian.R
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.data.model.Tag
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.view.TagDataView

class CategoryFragment() : Fragment() {
    private val fenlei: Fenlei? by lazy {
        arguments?.getParcelable(ARG_FENLEI)
    }
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding
    private var isDataLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        if (fenlei == null) return
        lifecycleScope.launch {
            val slideData =viewModel.requestSlideData(fenlei!!.id)
            binding.slideView.setVideoList(slideData, true)
        }
        lifecycleScope.launch {
            val customTags =viewModel.requestCustomTags(fenlei!!.id)
            if (customTags.isNotEmpty()) setupCustomTagsView(customTags)
        }
        /*lifecycleScope.launch {
            val tagDatas =viewModel.requestTagDatas(fenlei!!.id)
            for (tagData in tagDatas){
                val tagDataView = TagDataView(requireContext())
                tagDataView.setTagData(AppData.getInstance(requireContext()).imgDomains,tagData)
                binding.main.addView(tagDataView)
            }
        }*/
    }

    private fun setupCustomTagsView(customTags:List<Tag>) {
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
        fun newInstance(fenlei: Fenlei): CategoryFragment {
            return CategoryFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_FENLEI, fenlei) }
            }
        }
    }
}