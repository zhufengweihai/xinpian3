package uni.zf.xinpian.search

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.recommendUrl
import uni.zf.xinpian.data.AppConst.searchCategoryUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.Category
import uni.zf.xinpian.json.model.RecommendItem
import uni.zf.xinpian.utils.createHeaders

class SearchViewModel : ViewModel() {
    suspend fun getSearchRecommend(context: Context): List<RecommendItem> {
        try {
            val json = OkHttpUtil.get(recommendUrl, createHeaders(context, recommendUrl))
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            return dataArray?.map { Json.decodeFromJsonElement<RecommendItem>(it) } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
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