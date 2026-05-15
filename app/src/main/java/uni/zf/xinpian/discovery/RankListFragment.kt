package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.databinding.FragmentRankListBinding

fun newRankListFragment(categoryId: Int) = RankListFragment().apply {
    arguments = Bundle().apply {
        putInt(ARG_CATEGORY, categoryId)
    }
}

class RankListFragment : Fragment() {
    private var binding: FragmentRankListBinding? = null
    private lateinit var adapter: RankListAdapter
    private val viewModel: RankViewModel by viewModels()
    private var hasLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRankListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RankListAdapter()
        binding!!.rvRankList.adapter = adapter

        binding!!.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        if (!hasLoaded) {
            binding!!.swipeRefresh.isRefreshing = true
            lifecycleScope.launch {
                adapter.updateItems(viewModel.getWeekRankList())
                binding!!.swipeRefresh.isRefreshing = false
            }
            hasLoaded = true
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            adapter.updateItems(viewModel.getWeekRankList())
            binding!!.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
