package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import uni.zf.xinpian.databinding.FragmentRankingBinding
import uni.zf.xinpian.json.model.WeekRankOption

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiscoverViewModel by viewModels()
    private var rankOptions: List<WeekRankOption> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRanking.layoutManager = LinearLayoutManager(context)
        loadOptions()
    }

    private fun loadOptions() {
        lifecycleScope.launch {
            rankOptions = viewModel.getWeekRankOptions()
            val filterNames = rankOptions.map { it.name }
            
            val filterAdapter = ScheduleFilterAdapter(filterNames, ::loadRankList)
            binding.rvFilters.adapter = filterAdapter
            
            if (rankOptions.isNotEmpty()) {
                loadRankList(rankOptions[0].categoryId)
            }
        }
    }

    private fun loadRankList(categoryId: Int) {
        lifecycleScope.launch {
            val list = viewModel.getWeekRankList(categoryId)
            binding.rvRanking.adapter = RankingAdapter(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}