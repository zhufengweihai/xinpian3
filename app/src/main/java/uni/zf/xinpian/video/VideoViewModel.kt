package uni.zf.xinpian.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import uni.zf.xinpian.data.model.Video

class VideoViewModel : ViewModel() {
    private val queryParams = MutableStateFlow(QueryParams(query = "", orderBy = DEFAULT_ORDER_BY))

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataFlow: Flow<PagingData<Video>> = queryParams.flatMapLatest {
        val config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = DISTANCE, initialLoadSize = PAGE_SIZE)
        Pager(config) { VideoPagingSource(it.query, it.orderBy, PAGE_SIZE) }.flow.cachedIn(viewModelScope)
    }

    fun updateQueryParams(query: String, orderBy: String = DEFAULT_ORDER_BY) {
        queryParams.value = QueryParams(query, orderBy)
    }

    companion object {
        private const val DEFAULT_ORDER_BY = "hotness desc"
        private const val PAGE_SIZE = 63
        private const val DISTANCE = 9
    }
}