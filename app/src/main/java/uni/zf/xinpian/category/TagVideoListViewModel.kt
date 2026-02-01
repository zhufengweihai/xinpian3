package uni.zf.xinpian.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.AppConst.ARG_DY_TAG
import uni.zf.xinpian.data.AppConst.ARG_TAG_TITLE
import uni.zf.xinpian.data.AppConst.tagVideoListUrl
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.list.VideoPagingSource
import uni.zf.xinpian.utils.createHeaders

class TagVideoListViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val id = ssh.get<Int>(ARG_DY_TAG) ?: 0
    val title = ssh.get<String>(ARG_TAG_TITLE) ?: ""

    val videoDataFlow: Flow<PagingData<TagData>> = Pager(
        PagingConfig(
            12,
            6,
            true,
            12,
            120
        )
    ) {
        VideoPagingSource(tagVideoListUrl.format(id), createHeaders(app, tagVideoListUrl))
    }.flow.cachedIn(viewModelScope)
}