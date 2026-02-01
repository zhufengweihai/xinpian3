package uni.zf.xinpian.discovery

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
import uni.zf.xinpian.databinding.FragmentRankListBinding
import uni.zf.xinpian.databinding.FragmentSearchCategoryBinding
import uni.zf.xinpian.discovery.RankListAdapter
import uni.zf.xinpian.discovery.RankViewModel

fun newRankListFragment(categoryId: Int) = RankListFragment().apply {
    arguments = Bundle().apply {
        putInt(ARG_CATEGORY, categoryId)
    }
}

class RankListFragment : Fragment() {
    private var binding: FragmentRankListBinding? = null
    private lateinit var adapter: RankListAdapter
    private val viewModel: RankViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRankListBinding.inflate(inflater, container, false)
        init()
        return binding!!.root
    }

    private fun init() {
        adapter = RankListAdapter()
        binding!!.rvRankList.adapter = adapter
        lifecycleScope.launch {
            adapter.updateItems(viewModel.getWeekRankList())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}