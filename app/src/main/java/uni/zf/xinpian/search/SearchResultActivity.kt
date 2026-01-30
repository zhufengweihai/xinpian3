package uni.zf.xinpian.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import uni.zf.xinpian.databinding.ActivitySearchResultBinding
import uni.zf.xinpian.json.model.Category

class SearchResultActivity : AppCompatActivity() {
    private val binding: ActivitySearchResultBinding by lazy { ActivitySearchResultBinding.inflate(layoutInflater) }
    private val viewModel: CommonResultViewModel by viewModels()

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
        loadData()
    }

    private fun setupListeners() {
        binding.clearView.setOnClickListener { binding.inputView.text.clear() }
        binding.searchView.setOnClickListener { handleSearchAction() }
    }

    private fun initInputView() {
        binding.inputView.setText(viewModel.originalKeyword)
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

    private fun searchVideo(keyword: String) {
        viewModel.updateKeyword(keyword)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputView.windowToken, 0)
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

        override fun createFragment(position: Int) = newSearchResultFragment(
            categoryList[position].id, viewModel
                .originalKeyword
        )
    }

    private fun loadData() {
        lifecycleScope.launch {
            initTabCategory(viewModel.getCategoryList(this@SearchResultActivity))
        }
    }
}