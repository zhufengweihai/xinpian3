package uni.zf.xinpian.json

import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.VideoData
import java.io.InputStream
import java.io.OutputStream

class VideoSerializer : Serializer<VideoData?> {
    override val defaultValue = null

    override suspend fun readFrom(input: InputStream): VideoData? {
        return try {
            Json.decodeFromString<VideoData>(input.readBytes().decodeToString())
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: VideoData?, output: OutputStream) {
        output.write(Json.encodeToString(value = t).encodeToByteArray())
    }
}