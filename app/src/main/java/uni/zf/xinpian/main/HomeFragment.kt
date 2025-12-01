package uni.zf.xinpian.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.FragmentHomeBinding
import uni.zf.xinpian.recommend.RecFragment
import uni.zf.xinpian.series.SeriesFragment

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupViewPagerAndTabs(binding)
        return binding.root
    }

    private fun setupViewPagerAndTabs(binding: FragmentHomeBinding) {
        val titles = resources.getStringArray(R.array.tab_rec_list)
        binding.viewPager.apply {
            adapter = createSectionsAdapter()
            isUserInputEnabled = false
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            tab.text = titles.getOrNull(pos) ?: "Tab $pos"
        }.attach()
    }

    private fun createSectionsAdapter() = object : FragmentStateAdapter(this) {
        override fun getItemCount() = TAB_COUNT

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> RecFragment.newInstance()
            in 1 until TAB_COUNT -> SeriesFragment.newInstance(categories[position - 1])
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    companion object {
        private val categories = arrayOf("M", "S", "V", "A", "D")
        private const val TAB_COUNT = 6 // RecFragment + 5 categories
    }
}