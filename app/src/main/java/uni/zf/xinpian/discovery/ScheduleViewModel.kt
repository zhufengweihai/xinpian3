package uni.zf.xinpian.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.json.model.DateMovieGroup

class ScheduleViewModel : ViewModel() {
    // 如果需要获取即将上映的数据，可以使用这个数据流
    // 这里假设使用 DiscoverPagingSource 来获取数据，或者创建一个专门的 SchedulePagingSource
    val scheduleDataFlow: Flow<PagingData<DateMovieGroup>> = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            initialLoadSize = 20
        ),
        pagingSourceFactory = { DiscoverPagingSource() }
    ).flow.cachedIn(viewModelScope)
}