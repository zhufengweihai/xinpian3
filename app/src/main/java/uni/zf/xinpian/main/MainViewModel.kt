package uni.zf.xinpian.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.AppConst
import uni.zf.xinpian.data.AppConst.imgDomainUrl
import uni.zf.xinpian.data.AppConst.initUrl
import uni.zf.xinpian.json.categoryDataStore
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.json.model.CategoryList
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val categoryDataStore = getApplication<Application>().categoryDataStore

    fun getCategoryList() = categoryDataStore.data

    fun refreshSecret(context: Context) {
        viewModelScope.launch {
            val json = requestUrl(initUrl, createHeaders(context))
            if (json.isEmpty()) return@launch
            val jsonObject = Json.parseToJsonElement(json).jsonObject
            val secret = jsonObject["data"]?.jsonObject["secret"]?.toString() ?: ""
            AppData.getInstance(context).secret = secret
        }
    }

    fun refreshImgDomains(context: Context) {
        viewModelScope.launch {
            val json = requestUrl(imgDomainUrl, createHeaders(context))
            if (json.isEmpty()) return@launch
            val jsonObject = Json.parseToJsonElement(json).jsonObject
            val imgDomainString = jsonObject["data"]?.jsonObject["imgDomain"]?.toString() ?: ""
            val imgDomains = imgDomainString.split(",")
            AppData.getInstance(context).imgDomains = imgDomains
        }
    }

    fun requestCategoryList() {
        viewModelScope.launch {
            val json = requestUrl(AppConst.fenleiUrl)
            if (json.isEmpty()) return@launch
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val categoryList = dataArray?.map { Json.decodeFromJsonElement<Category>(it) } ?: emptyList()
            categoryDataStore.updateData { CategoryList(categoryList) }
        }
    }
}