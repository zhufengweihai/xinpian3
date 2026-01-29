package uni.zf.xinpian.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.ARG_KEYWORD
import uni.zf.xinpian.data.AppConst.searchUrl
import uni.zf.xinpian.json.model.SearchListItem
import uni.zf.xinpian.json.model.SearchResultItem

class SearchResultViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val categoryId = ssh.get<Int>(ARG_CATEGORY) ?: 0
    private val keyword = ssh.get<String>(ARG_KEYWORD) ?: ""


    val videoFlow: Flow<PagingData<SearchResultItem>> = Pager(
        PagingConfig(
            20,
            3,
            true,
            20,
            100
        )
    ) { SearchResultPagingSource(searchUrl.format(keyword, categoryId)) }.flow.cachedIn(viewModelScope)
}