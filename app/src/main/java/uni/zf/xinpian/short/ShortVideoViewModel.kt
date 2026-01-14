package uni.zf.xinpian.short

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.json.model.ShortVideo

class ShortVideoViewModel(app: Application) : AndroidViewModel(app) {
    val dataFlow: Flow<PagingData<ShortVideo>> = Pager(
        PagingConfig(
            10,
            5,
            true,
            10,
            100
        ),
    ) {
        ShortVideoPagingSource(
        )
    }.flow.cachedIn(viewModelScope)
}