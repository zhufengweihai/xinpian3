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
import uni.zf.xinpian.data.AppConst.categoryVideoUrl
import uni.zf.xinpian.json.model.VideoData

class CategoryViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val categoryId = ssh.get<Int>(ARG_CATEGORY) ?: 0

    val videoFlow: Flow<PagingData<VideoData>> = Pager(
        PagingConfig(
            10,
            3,
            true,
            10,
            100
        )
    ) { VideoPagingSource(categoryVideoUrl.format(categoryId)) }.flow.cachedIn(viewModelScope)
}