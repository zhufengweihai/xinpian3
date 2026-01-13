package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import uni.zf.xinpian.databinding.FragmentDiscoveryBinding

class DiscoveryFragment : Fragment() {

    private var _binding: FragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf("上映表", "专题", "排行榜")
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = tabs.size

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> ScheduleFragment()
                    1 -> SpecialsFragment()
                    2 -> RankingFragment()
                    else -> Fragment()
                }
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
        
        // Default to "Ranking" based on the first image if needed, 
        // but usually we start from the first one. 
        // Image 1 shows "排行榜" selected, Image 2 "专题", Image 3 "上映表".
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}