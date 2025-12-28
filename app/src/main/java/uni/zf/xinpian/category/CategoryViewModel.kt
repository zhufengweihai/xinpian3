package uni.zf.xinpian.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.json.createCustomTagDataStore
import uni.zf.xinpian.json.createDyTagDataStore
import uni.zf.xinpian.json.createSlideDataStore
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.json.model.CustomTags
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.json.model.DyTagList
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.json.model.SlideList
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel(app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val category: Category = ssh.get<Category>(arg_category) ?: Category()
    private val app = getApplication<Application>()
    private val slideDataStore = app.createSlideDataStore(category.id)
    private val customTagDataStore = app.createCustomTagDataStore(category.id)
    private val dyTagDataStore = app.createDyTagDataStore(category.id)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSlideList() = slideDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCustomTagList() = customTagDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDyTagList() = dyTagDataStore.data

    fun requestSlideData() {
        viewModelScope.launch {
            val json = requestUrl(slideUrl.format(category.id), createHeaders(app))
            if (json.isEmpty()) return@launch
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val slideList = dataArray?.map { Json.decodeFromJsonElement<SlideData>(it) } ?: emptyList()
            slideDataStore.updateData { SlideList(slideList) }
        }
    }

    fun requestCustomTags() {
        viewModelScope.launch {
            val json = requestUrl(tagsUrl.format(category.id), createHeaders(app))
            if (json.isEmpty()) return@launch
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val customTags = dataArray?.map { Json.decodeFromJsonElement<CustomTag>(it) } ?: emptyList()
            customTagDataStore.updateData { CustomTags(customTags) }
        }
    }

    fun requestDyTag() {
        viewModelScope.launch {
            val json = requestUrl(dyTagURL.format(app), createHeaders(app))
            if (json.isEmpty()) return@launch
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val dyTagDatas = dataArray?.map { Json.decodeFromJsonElement<DyTag>(it) } ?: emptyList()
            dyTagDataStore.updateData { DyTagList(list = dyTagDatas) }
        }
    }
}