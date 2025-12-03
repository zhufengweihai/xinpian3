package uni.zf.xinpian.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.data.model.RecData
import uni.zf.xinpian.data.model.Tag
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.databinding.FragmentCategoryBinding
import uni.zf.xinpian.main.VideoDataViewModel
import uni.zf.xinpian.search.SearchParamAdapter
import uni.zf.xinpian.search.SearchParamListener
import uni.zf.xinpian.search.SearchType
import uni.zf.xinpian.search.SearchType.GENRES
import uni.zf.xinpian.series.SeriesItemDecoration
import uni.zf.xinpian.video.EvenVideoListAdapter

class CategoryFragment(private val fenlei: Fenlei) : Fragment() {

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
        lifecycleScope.launch {
            val slideData =viewModel.requestSlideData(fenlei.id)
            binding.slideView.setVideoList(slideData, true)
        }
        lifecycleScope.launch {
            val customTags =viewModel.requestCustomTags(fenlei.id)
            if (customTags.isNotEmpty()) setupCustomTagsView(customTags)
        }
        lifecycleScope.launch {
            val byTagData =viewModel.requestByTagData(fenlei.id)
            if (customTags.isNotEmpty()) setupCustomTagsView(customTags)
        }
    }

    private fun setupCustomTagsView(customTags:List<Tag>) {
        val adapter = TagAdapter(customTags)
        binding.tagListView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(SeriesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
            this.adapter = adapter
        }
    }

    companion object {
        private const val ARG_FENLEI = "fenlei"

        @JvmStatic
        fun newInstance(fenlei: Fenlei): CategoryFragment {
            return CategoryFragment(fenlei).apply {
                arguments = Bundle().apply { putSerializable(ARG_FENLEI, fenlei) }
            }
        }
    }
}