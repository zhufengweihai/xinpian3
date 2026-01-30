package uni.zf.xinpian.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.ARG_KEYWORD
import uni.zf.xinpian.data.AppConst.searchCategoryUrl
import uni.zf.xinpian.data.AppConst.searchUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.json.model.SearchResultItem
import uni.zf.xinpian.search.SearchResultPagingSource
import uni.zf.xinpian.utils.createHeaders

class CommonResultViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    val originalKeyword = ssh.get<String>(ARG_KEYWORD) ?: ""
    private val _keyword = MutableLiveData(originalKeyword)
    val keyword = _keyword

    fun updateKeyword(newKeyword: String) {
        _keyword.value = newKeyword
    }

    suspend fun getCategoryList(context: Context): List<Category> {
        try {
            val json = OkHttpUtil.get(searchCategoryUrl, createHeaders(context, searchCategoryUrl))
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            return dataArray?.map { Json.decodeFromJsonElement<Category>(it) } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }
}