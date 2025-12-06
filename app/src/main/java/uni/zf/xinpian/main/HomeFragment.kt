package uni.zf.xinpian.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import uni.zf.xinpian.category.CategoryFragment
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.requestImgDomains()
        }
        lifecycleScope.launch {
            val fenleiList = viewModel.requestFenlei()
            setupViewPagerAndTabs(fenleiList)
        }
    }

    private fun setupViewPagerAndTabs(fenleiList: List<Fenlei>) {
        binding.apply {
            viewPager.adapter = createSectionsAdapter(fenleiList)
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(tabs, viewPager) { tab, pos -> tab.text = fenleiList[pos].name }.attach()
        }
    }

    private fun createSectionsAdapter(fenleiList: List<Fenlei>) = object : FragmentStateAdapter(this) {
        override fun getItemCount() = fenleiList.size

        override fun createFragment(position: Int): Fragment = CategoryFragment.newInstance(fenleiList[position])
    }
}