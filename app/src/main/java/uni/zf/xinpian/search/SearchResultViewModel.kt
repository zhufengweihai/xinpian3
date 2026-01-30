package uni.zf.xinpian.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.ARG_KEYWORD
import uni.zf.xinpian.data.AppConst.searchUrl
import uni.zf.xinpian.json.model.SearchResultItem

class SearchResultViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val categoryId = ssh.get<Int>(ARG_CATEGORY) ?: 0
    private val keyword = MutableStateFlow(ssh.get<String>(ARG_KEYWORD) ?: "")

    fun updateKeyword(newKeyword: String) {
        keyword.value = newKeyword
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoFlow: Flow<PagingData<SearchResultItem>> = keyword.flatMapLatest {
        Pager(
            PagingConfig(
                20,
                3,
                true,
                20,
                100
            )
        ) { SearchResultPagingSource(searchUrl.format(it, categoryId)) }.flow.cachedIn(viewModelScope)
    }
}