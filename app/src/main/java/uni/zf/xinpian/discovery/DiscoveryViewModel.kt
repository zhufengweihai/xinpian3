package uni.zf.xinpian.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.json.model.MovieResponse

class DiscoveryViewModel() : ViewModel() {
    val soonDataFlow: Flow<PagingData<MovieResponse>> = Pager(
        PagingConfig(
            20,
            5,
            true,
            20,
            100
        ), pagingSourceFactory = ::DiscoverPagingSource).flow.cachedIn(viewModelScope)
}