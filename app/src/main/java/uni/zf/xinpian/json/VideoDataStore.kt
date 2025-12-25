package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
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

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    override suspend fun readFrom(input: InputStream): VideoData {
        return json.decodeFromString(
            deserializer = VideoData.serializer(),
            string = input.readBytes().decodeToString()
        )
    }

    override suspend fun writeTo(t: VideoData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = VideoData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}