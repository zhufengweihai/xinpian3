package uni.zf.xinpian.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.CustomTag
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.json.model.SlideData
import uni.zf.xinpian.utils.createHeaders

class CategoryViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val categoryId = ssh.get<Int>(ARG_CATEGORY) ?: 0

    suspend fun fetchSlideData(): List<SlideData> {
        return try {
            val json = OkHttpUtil.get(slideUrl.format(categoryId), createHeaders(app))
            if (json.isEmpty()) return emptyList()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            dataArray?.map { Json.decodeFromJsonElement<SlideData>(it) } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchCustomTags(): List<CustomTag> {
        return try {
            val json = OkHttpUtil.get(tagsUrl.format(categoryId), createHeaders(app))
            if (json.isEmpty()) return emptyList()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            dataArray?.map { Json.decodeFromJsonElement<CustomTag>(it) } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchDyTags(): List<DyTag> {
        return try {
            val json = OkHttpUtil.get(dyTagURL.format(categoryId), createHeaders(app))
            if (json.isEmpty()) return emptyList()
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val dataArray = fullJsonObject["data"]?.jsonArray
            dataArray?.map { Json.decodeFromJsonElement<DyTag>(it) } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
