package uni.zf.xinpian.search

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.ARG_KEYWORD
import uni.zf.xinpian.data.model.SearchHistory
import uni.zf.xinpian.databinding.ActivitySearchVideoBinding
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.view.SpaceItemDecoration

class SearchVideoActivity : AppCompatActivity(), SearchHistoryListener {
    private val binding: ActivitySearchVideoBinding by lazy { ActivitySearchVideoBinding.inflate(layoutInflater) }
    private lateinit var recommendAdapter: SearchRecommendAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val historyViewModel: SearchHistoryViewModel by viewModels()
    private val viewModel: SearchVideoViewModel by viewModels()
    private var categories: List<Category> = listOf()
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
        initSearchHistoryView()
        initRecommendSearchView()
        loadData()
    }

    private fun setupListeners() {
        binding.clearView.setOnClickListener { binding.inputView.text.clear() }
        binding.searchView.setOnClickListener { handleSearchAction() }
        binding.deleteView.setOnClickListener { toggleDeleteMode(true) }
        binding.finishView.setOnClickListener { toggleDeleteMode(false) }
        binding.deleteAllView.setOnClickListener { showDeleteAllConfirmation() }
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputView.windowToken, 0)
    }

    private fun searchVideo(keyword: String) {
        historyViewModel.saveSearchHistory(SearchHistory(keyword))
        val categoryId = categories[binding.vpCategory.currentItem].id
        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putExtra(ARG_CATEGORY, categoryId)
            putExtra(ARG_KEYWORD, keyword)
        }
        startActivity(intent)
    }

    private fun showDeleteAllConfirmation() {
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setMessage(R.string.delete_all_confirm)
            .setPositiveButton("确定") { _, _ ->
                historyViewModel.deleteAllSearchHistory()
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun toggleDeleteMode(delete: Boolean) {
        val visibility = if (delete) VISIBLE else GONE
        binding.deleteAllView.visibility = visibility
        binding.finishView.visibility = visibility
        binding.deleteView.visibility = if (delete) GONE else VISIBLE
        historyAdapter.setDeleteMode(delete)
    }

    private fun initSearchHistoryView() {
        binding.searchHistoryView.apply {
            layoutManager = GridLayoutManager(this@SearchVideoActivity, 4)
            addItemDecoration(SpaceItemDecoration(this@SearchVideoActivity))
            historyAdapter = SearchHistoryAdapter(this@SearchVideoActivity)
            adapter = historyAdapter
        }
        lifecycleScope.launch {
            historyViewModel.getAllSearchHistory().collect {
                historyAdapter.setHistories(it)
                if (it.isEmpty()) {
                    binding.deleteAllView.visibility = GONE
                    binding.historyLabel.visibility = GONE
                    binding.finishView.visibility = GONE
                    binding.deleteView.visibility = GONE
                    binding.searchHistoryView.visibility = GONE
                }
            }
        }
    }

    private fun initRecommendSearchView() {
        binding.recommendSearchView.apply {
            layoutManager = GridLayoutManager(this@SearchVideoActivity, 3)
            addItemDecoration(SpaceItemDecoration(this@SearchVideoActivity))
            recommendAdapter = SearchRecommendAdapter()
            adapter = recommendAdapter
        }
    }

    private fun initTabCategory(categories: List<Category>) {
        binding.vpCategory.adapter = createSectionsAdapter(categories)
        binding.vpCategory.isUserInputEnabled = false
        TabLayoutMediator(binding.tabCategory, binding.vpCategory) { tab, pos ->
            tab.text = categories[pos].name
        }.attach()
    }

    private fun createSectionsAdapter(categoryList: List<Category>) = object : FragmentStateAdapter(this) {
        override fun getItemCount() = categoryList.size

        override fun createFragment(position: Int): Fragment = newSearchCategoryFragment(categoryList[position].id)
    }

    private fun loadData() {
        lifecycleScope.launch {
            categories = viewModel.getCategoryList(this@SearchVideoActivity)
            initTabCategory(categories)
        }
        lifecycleScope.launch {
            recommendAdapter.updateItems(viewModel.getSearchRecommend(this@SearchVideoActivity))
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