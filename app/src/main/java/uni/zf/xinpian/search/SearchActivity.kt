package uni.zf.xinpian.search

import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.SearchHistory
import uni.zf.xinpian.databinding.ActivitySearchBinding
import uni.zf.xinpian.video.LargeVideoListAdapter
import uni.zf.xinpian.video.VideoListAdapter
import uni.zf.xinpian.video.VideoViewModel
import uni.zf.xinpian.view.SpaceItemDecoration

class SearchActivity : AppCompatActivity(), SearchHistoryListener {
    private val binding: ActivitySearchBinding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private lateinit var resultAdapter: VideoListAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val resultViewModel: VideoViewModel by viewModels()
    private val historyViewModel: SearchHistoryViewModel by viewModels()
    private var firstLoad = true

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
        initSearchResultView()
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
        resultViewModel.updateQueryParams("/$keyword/.test(name) || actors=='$keyword'")
        if (firstLoad) {
            firstLoad = false
            lifecycleScope.launch {
                resultViewModel.dataFlow.collectLatest {
                    resultAdapter.submitData(it)
                }
            }
        }
        historyViewModel.saveSearchHistory(SearchHistory(keyword))
    }

    private fun initSearchHistoryView() {
        binding.searchHistoryView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 4)
            addItemDecoration(SpaceItemDecoration(this@SearchActivity))
            historyAdapter = SearchHistoryAdapter(this@SearchActivity)
            adapter = historyAdapter
        }
        lifecycleScope.launch {
            historyViewModel.getAllSearchHistory().collect { historyAdapter.setHistories(it) }
        }
    }

    private fun initSearchResultView() {
        binding.searchResultView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            addItemDecoration(ResultItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
            resultAdapter = LargeVideoListAdapter()
            adapter = resultAdapter
        }
        resultAdapter.addLoadStateListener {
            binding.loadingView.visibility = if (it.refresh is Loading) VISIBLE else GONE
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