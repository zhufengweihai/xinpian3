package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.VideoData
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun Context.createVideoDataStore(id: Int) = DataStoreFactory.create(
    VideoSerializer(),
    produceFile = { File(filesDir, "video_$id.json") }
)

class VideoSerializer : Serializer<VideoData> {
    override val defaultValue = VideoData()

    override suspend fun readFrom(input: InputStream): VideoData {
        return try {
            Json.decodeFromString<VideoData>(input.readBytes().decodeToString())
        } catch (_: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: VideoData, output: OutputStream) {
        output.write(Json.encodeToString(value = t).encodeToByteArray())
    }
}