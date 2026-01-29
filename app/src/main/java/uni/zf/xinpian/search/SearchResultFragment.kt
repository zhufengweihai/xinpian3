package uni.zf.xinpian.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.databinding.FragmentSearchResultBinding
import kotlin.getValue

fun newSearchResultFragment(categoryId: Int) = SearchResultFragment().apply {
    arguments = Bundle().apply {
        putInt(ARG_CATEGORY, categoryId)
    }
}

class SearchResultFragment : Fragment() {
    private var binding: FragmentSearchResultBinding? = null
    private lateinit var adapter: CategoryVideoAdapter
    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
       init()
        return binding!!.root
    }

    private fun init() {
        adapter = CategoryVideoAdapter()
        binding!!.rvCategoryVideo.adapter = adapter
        lifecycleScope.launch {
            viewModel.videoFlow.collectLatest(adapter::submitData)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}