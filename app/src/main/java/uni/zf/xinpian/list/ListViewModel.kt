package uni.zf.xinpian.list

import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.filterOptionsUrl
import uni.zf.xinpian.data.AppConst.filteredVideoUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.FilterGroup

class ListViewModel() : ViewModel() {
    suspend fun getFilterGroups(): List<FilterGroup> {
        try {
            val json = OkHttpUtil.get(filterOptionsUrl)
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            return dataArray?.map { Json.decodeFromJsonElement<FilterGroup>(it) } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }

    suspend fun getfilteredVideos(key: String): List<String> {
        try {
            val json = OkHttpUtil.get(filteredVideoUrl)
            if (json.isEmpty()) return listOf()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            val filterGroup = dataArray?.map { Json.decodeFromJsonElement<FilterGroup>(it) }?.find { it.key == key }
            return filterGroup?.options?.map { it.name } ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }
}