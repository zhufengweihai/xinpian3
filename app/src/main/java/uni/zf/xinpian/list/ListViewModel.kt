package uni.zf.xinpian.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.filterOptionsUrl
import uni.zf.xinpian.data.AppConst.filteredVideoUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.FilterGroup
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.utils.createHeaders

class ListViewModel(val app: Application) : AndroidViewModel(app) {
    private val filterOptions = MutableStateFlow(FilterOptions())

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoFlow: Flow<PagingData<TagData>> = filterOptions.flatMapLatest {
        val config = PagingConfig(
            15,
            9,
            true,
            15,
            150
        )
        Pager(config) {
            VideoPagingSource(
                filteredVideoUrl.format(
                    it.fcatePid, it.categoryId, it.area, it.year,
                    it.type, it.sort
                )
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun updateFilterOptions(options: FilterOptions) {
        // 创建一个新的FilterOptions实例以确保StateFlow能检测到变化
        filterOptions.value = options.copy()
    }

    suspend fun getFilterGroups(): List<FilterGroup> {
        try {
            val json = OkHttpUtil.get(filterOptionsUrl, createHeaders(app, filterOptionsUrl))
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            return dataArray?.map { Json.decodeFromJsonElement<FilterGroup>(it) } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }
}