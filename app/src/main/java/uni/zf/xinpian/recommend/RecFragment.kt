package uni.zf.xinpian.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.FragmentRecBinding
import uni.zf.xinpian.main.VideoDataViewModel

class RecFragment : Fragment() {
    private val viewModel: VideoDataViewModel by activityViewModels()
    private var _binding: FragmentRecBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        binding.recCombinedView.adapter = RecDataAdapter()
        binding.recCombinedView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        val titles = resources.getStringArray(R.array.title_rec_list)
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.recData.observe(viewLifecycleOwner) {
            if (!it.isEmpty()) (binding.recCombinedView.adapter as RecDataAdapter).setData(it, titles)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel.syncVideoData(requireContext(), true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = RecFragment()
    }
}