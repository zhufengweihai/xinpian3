package uni.zf.xinpian.shorts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import uni.zf.xinpian.data.AppConst.ARG_SHORT_ID
import uni.zf.xinpian.data.AppConst.shortUrl
import uni.zf.xinpian.http.OkHttpUtil
import uni.zf.xinpian.json.model.ShortVideo

class ShortPlayViewModel(app: Application, ssh: SavedStateHandle) : AndroidViewModel(app) {
    private val vid = ssh.get<Int>(ARG_SHORT_ID) ?: 0
    suspend fun getShortVideo(): ShortVideo? {
        try {
            val json = OkHttpUtil.get(shortUrl.format(vid))
            if (json.isEmpty()) return null
            val fullJsonObject = Json.parseToJsonElement(json).jsonObject
            val data = fullJsonObject["data"]?.jsonObject
            return data?.let { Json.decodeFromJsonElement<ShortVideo>(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}