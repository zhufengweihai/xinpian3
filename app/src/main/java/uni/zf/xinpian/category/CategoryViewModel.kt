package uni.zf.xinpian.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.data.model.Category
import uni.zf.xinpian.json.createCustomTagDataStore
import uni.zf.xinpian.json.createDyTagDataStore
import uni.zf.xinpian.json.createSlideDataStore
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.json.model.CustomTags
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.json.model.SlideList
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val category: Category = savedStateHandle.get<Category>(arg_category) ?: Category()
    private val app = getApplication<Application>()
    private val slideDataStore = app.createSlideDataStore(category.id)
    private val customTagDataStore = app.createCustomTagDataStore(category.id)
    private val dyTagDataStore = app.createDyTagDataStore(category.id)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSlideList(): Flow<SlideList> = slideDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCustomTagList() = customTagDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDyTag() = dyTagDataStore.data

    fun requestSlideData() {
        viewModelScope.launch {
            val json = requestUrl(slideUrl.format(category.id), createHeaders(app))
            val gson = Gson()
            val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
            val dataArray = fullJsonObject.getAsJsonArray("data")
            val slideList = dataArray.map { gson.fromJson(it, SlideData::class.java) }
            slideDataStore.updateData { SlideList(slideList) }
        }
    }

    fun requestCustomTags() {
        viewModelScope.launch {
            val json = requestUrl(tagsUrl.format(category.id), createHeaders(app))
            val gson = Gson()
            val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
            val dataArray = fullJsonObject.getAsJsonArray("data")
            val customTags = dataArray.map { gson.fromJson(it, CustomTag::class.java) }
            customTagDataStore.updateData { CustomTags(customTags) }
        }
    }

    fun requestDyTag() {
        viewModelScope.launch {
            val json = requestUrl(dyTagURL.format(app), createHeaders(app))
            val gson = Gson()
            val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
            val dataArray = fullJsonObject.getAsJsonArray("data")
            val tagDatas = dataArray.map { gson.fromJson(it, TagData::class.java) }
            dyTagDataStore.updateData { DyTag(dataList = tagDatas) }
        }
    }
}