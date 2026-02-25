package uni.zf.xinpian.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.ARG_KEYWORD
import uni.zf.xinpian.databinding.FragmentSearchResultBinding

fun newSearchResultFragment(categoryId: Int, keyword: String) = SearchResultFragment().apply {
    arguments = Bundle().apply {
        putInt(ARG_CATEGORY, categoryId)
        putString(ARG_KEYWORD, keyword)
    }
}

class SearchResultFragment : Fragment() {
    private var binding: FragmentSearchResultBinding? = null
    private lateinit var adapter: SearchResultAdapter
    private val viewModel: SearchResultViewModel by viewModels()
    private val commonViewModel: CommonResultViewModel by activityViewModels()
    private var hasLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchResultAdapter()
        binding!!.rvCategoryVideo.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onPause() {
        super.onPause()
        commonViewModel.keyword.removeObservers(viewLifecycleOwner)
    }

    private fun loadData() {
        if (hasLoaded) {
            commonViewModel.keyword.observe(viewLifecycleOwner, {
                viewModel.updateKeyword(it)
                adapter.updateKeyword(it)
            })
            lifecycleScope.launch {
                viewModel.videoFlow.collectLatest(adapter::submitData)
            }
            hasLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}