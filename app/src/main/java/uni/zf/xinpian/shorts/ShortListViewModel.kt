package uni.zf.xinpian.shorts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.json.model.ShortVideo

class ShortListViewModel(app: Application) : AndroidViewModel(app) {
    val dataFlow: Flow<PagingData<ShortVideo>> = Pager(
        PagingConfig(
            10,
            6,
            true,
            10,
            100
        ), pagingSourceFactory = ::ShortListPagingSource).flow.cachedIn(viewModelScope)
}