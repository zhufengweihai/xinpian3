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
import uni.zf.xinpian.databinding.FragmentSearchCategoryBinding

fun newSearchCategoryFragment(categoryId: Int) = SearchCategoryFragment().apply {
    arguments = Bundle().apply {
        putInt(ARG_CATEGORY, categoryId)
    }
}

class SearchCategoryFragment : Fragment() {
    private var binding: FragmentSearchCategoryBinding? = null
    private lateinit var adapter: CategoryVideoAdapter
    private val viewModel: CategoryViewModel by viewModels()
    private var hasLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchCategoryBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoryVideoAdapter()
        binding!!.rvCategoryVideo.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
    private fun loadData() {
        if (!hasLoaded) {
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