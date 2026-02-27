package uni.zf.xinpian.main

import android.content.Intent
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
import uni.zf.xinpian.history.WatchHistoryActivity
import uni.zf.xinpian.json.model.CategoryList
import uni.zf.xinpian.list.ListActivity
import uni.zf.xinpian.search.SearchVideoActivity

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var hasLoaded = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputView.setOnClickListener {
            startActivity(Intent(requireContext(), SearchVideoActivity::class.java))
        }
        binding.ivHistory.setOnClickListener {
            startActivity(Intent(requireContext(), WatchHistoryActivity::class.java))
        }
        binding.ivList.setOnClickListener {
            startActivity(Intent(requireContext(), ListActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        if (!hasLoaded) {
            viewModel.requestCategoryList()
            lifecycleScope.launch {
                viewModel.getCategoryList().collect(::setupViewPagerAndTabs)
            }
            hasLoaded = true
        }
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