package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScheduleListAdapter
    private val viewModel: ScheduleViewModel by viewModels()
    private var hasLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScheduleListAdapter()
        binding.rvSchedule.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        if (!hasLoaded) {
            binding.swipeRefresh.isRefreshing = true
            lifecycleScope.launch {
                viewModel.scheduleDataFlow.collectLatest {
                    adapter.submitData(it)
                }
            }
            hasLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
