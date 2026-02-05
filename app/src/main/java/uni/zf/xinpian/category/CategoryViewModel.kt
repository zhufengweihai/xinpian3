package uni.zf.xinpian.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.App
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.json.model.CustomTags
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.json.model.DyTagList
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.json.model.SlideList
import uni.zf.xinpian.utils.createHeaders

class CategoryViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val categoryId = ssh.get<Int>(ARG_CATEGORY) ?: 0
    private val slideDataStore =  (app as App).getSlideDataStore(categoryId)
    private val customTagDataStore = (app as App).getCustomTagDataStore(categoryId)
    private val dyTagDataStore = (app as App).getDyTagDataStore(categoryId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSlideList() = slideDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCustomTagList() = customTagDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDyTagList() = dyTagDataStore.data

    fun requestSlideData() {
        viewModelScope.launch {
            try {
                val json = OkHttpUtil.get(slideUrl.format(categoryId), createHeaders(app))
                if (json.isEmpty()) return@launch
                val fullJsonObject = Json.parseToJsonElement(json).jsonObject
                val dataArray = fullJsonObject["data"]?.jsonArray
                val slideList = dataArray?.map { Json.decodeFromJsonElement<SlideData>(it) } ?: emptyList()
                slideDataStore.updateData { SlideList(slideList) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestCustomTags() {
        viewModelScope.launch {
            try {
                val json = OkHttpUtil.get(tagsUrl.format(categoryId), createHeaders(app))
                if (json.isEmpty()) return@launch
                val fullJsonObject = Json.parseToJsonElement(json).jsonObject
                val dataArray = fullJsonObject["data"]?.jsonArray
                val customTags = dataArray?.map { Json.decodeFromJsonElement<CustomTag>(it) } ?: emptyList()
                customTagDataStore.updateData { CustomTags(customTags) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestDyTag() {
        viewModelScope.launch {
            try {
                val json = OkHttpUtil.get(dyTagURL.format(categoryId), createHeaders(app))
                if (json.isEmpty()) return@launch
                val fullJsonObject = Json.parseToJsonElement(json).jsonObject
                val dataArray = fullJsonObject["data"]?.jsonArray
                val dyTagDatas = dataArray?.map { Json.decodeFromJsonElement<DyTag>(it) } ?: emptyList()
                dyTagDataStore.updateData { DyTagList(list = dyTagDatas) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun isDataLoaded(): Boolean {
        // 检查所有数据存储是否已有数据，避免每次都重新加载
        return runBlocking {
            val slideData = slideDataStore.data.firstOrNull()
            val customTagData = customTagDataStore.data.firstOrNull()
            val dyTagData = dyTagDataStore.data.firstOrNull()
            
            slideData != null && customTagData != null && dyTagData != null
        }
    }
}