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
import uni.zf.xinpian.category.newCategoryFragment
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.databinding.FragmentHomeBinding
import uni.zf.xinpian.json.model.CategoryList

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    private fun loadData() {
        viewModel.requestCategoryList()
        lifecycleScope.launch { viewModel.getCategoryList().collect(::setupViewPagerAndTabs) }
    }

    private fun setupViewPagerAndTabs(categories: CategoryList) {
        binding.apply {
            viewPager.adapter = createSectionsAdapter(categories.list)
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(tabs, viewPager) { tab, pos -> tab.text = categories.list[pos].name }.attach()
        }
    }

    private fun createSectionsAdapter(categoryList: List<Category>) = object : FragmentStateAdapter(this) {
        override fun getItemCount() = categoryList.size

        override fun createFragment(position: Int): Fragment = newCategoryFragment(categoryList[position].id)
    }
}