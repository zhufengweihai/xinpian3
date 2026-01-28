package uni.zf.xinpian.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import uni.zf.xinpian.data.model.SearchHistory
import uni.zf.xinpian.databinding.ActivitySearchVideoBinding
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.view.SpaceItemDecoration

class SearchVideoActivity : AppCompatActivity(), SearchHistoryListener {
    private val binding: ActivitySearchVideoBinding by lazy { ActivitySearchVideoBinding.inflate(layoutInflater) }
    private lateinit var recommendAdapter: SearchRecommendAdapter
    private val historyViewModel: SearchHistoryViewModel by viewModels()
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupListeners()
        initInputView()
        initRecommendSearchView()
        initCategoryViewPager2()
        loadData()
    }

    private fun setupListeners() {
        binding.clearView.setOnClickListener { binding.inputView.text.clear() }
        binding.searchView.setOnClickListener { handleSearchAction() }
    }

    private fun initInputView() {
        binding.inputView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleSearchAction()
                true
            } else false
        }
    }

    private fun handleSearchAction() {
        val inputText = binding.inputView.text.toString()
        if (inputText.length < 2) {
            Toast.makeText(this, "至少输入两个关键字！", Toast.LENGTH_SHORT).show()
        } else {
            searchVideo(inputText)
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputView.windowToken, 0)
    }

    private fun searchVideo(keyword: String) {
        historyViewModel.saveSearchHistory(SearchHistory(keyword))
        val intent = Intent(this, SearchResultActivity::class.java).apply {

        }
        startActivity(intent)
    }

    private fun initRecommendSearchView() {
        binding.recommendSearchView.apply {
            layoutManager = GridLayoutManager(this@SearchVideoActivity, 4)
            addItemDecoration(SpaceItemDecoration(this@SearchVideoActivity))
            recommendAdapter = SearchRecommendAdapter()
            adapter = recommendAdapter
        }
    }

    private fun initTabCategory(categories: List<Category>) {
        TabLayoutMediator(binding.tabCategory, binding.vpCategory) { tab, pos ->
            tab.text = categories[pos].name
        }.attach()
        binding.vpCategory.adapter = createSectionsAdapter(categories)
    }

    private fun createSectionsAdapter(categoryList: List<Category>) = object : FragmentStateAdapter(this) {
        override fun getItemCount() = categoryList.size

        override fun createFragment(position: Int): Fragment = newInstance(categoryList[position].id)
    }

    private fun initCategoryViewPager2() {
        binding.vpCategory.apply {
            isUserInputEnabled = false
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            initTabCategory(viewModel.getCategoryList(this@SearchVideoActivity))
        }
    }

    override fun onSearchHistory(history: String, delete: Boolean) {
        if (delete) {
            historyViewModel.deleteSearchHistory(history)
        } else {
            searchVideo(history)
        }
    }
}