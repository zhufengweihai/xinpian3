package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.FragmentRankingBinding
import uni.zf.xinpian.json.model.WeekRankOption

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RankViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        loadOptions()
        return binding.root
    }

    private fun loadOptions() {
        lifecycleScope.launch {
            setupViewPagerAndTabs(viewModel.getWeekRankOptions())
        }
    }

    private fun setupViewPagerAndTabs(rankOptions: List<WeekRankOption>) {
        binding.apply {
            vpRank.adapter = createSectionsAdapter(rankOptions)
            TabLayoutMediator(tabs, vpRank) { tab, pos ->
                val customView = LayoutInflater.from(context).inflate(R.layout.item_rank_tab, null) as TextView
                customView.text = rankOptions[pos].name
                tab.customView = customView
            }.attach()
        }
    }

    private fun createSectionsAdapter(rankOptions: List<WeekRankOption>) = object : FragmentStateAdapter(this) {
        override fun getItemCount() = rankOptions.size

        override fun createFragment(position: Int) = newRankListFragment(rankOptions[position].categoryId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}