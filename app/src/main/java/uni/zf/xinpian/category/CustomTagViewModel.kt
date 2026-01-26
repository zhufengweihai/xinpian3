package uni.zf.xinpian.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.ARG_TAG_URL
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.TopicDetail
import uni.zf.xinpian.utils.createHeaders

class CustomTagViewModel(val app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val tagDataUrl = ssh.get<String>(ARG_TAG_URL) ?: ""

    private val jsonConfig = Json {
        isLenient = true  // 允许非严格格式的 JSON
        ignoreUnknownKeys = true  // 忽略未知键
    }

    suspend fun getCustomTagData(): TopicDetail? {
        try {
            val json = OkHttpUtil.get(tagDataUrl, createHeaders(app, tagDataUrl))
            if (json.isEmpty()) return null
            val fullJsonObject = jsonConfig.parseToJsonElement(json).jsonObject
            val data = fullJsonObject["data"]?.jsonObject
            return data?.let { jsonConfig.decodeFromJsonElement<TopicDetail>(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}