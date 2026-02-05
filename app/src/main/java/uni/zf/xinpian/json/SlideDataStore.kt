package uni.zf.xinpian.json

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.SlideList
import java.io.InputStream
import java.io.OutputStream

class SlideListSerializer : Serializer<SlideList> {
    override val defaultValue = SlideList()

    override suspend fun readFrom(input: InputStream): SlideList {
        return try {
            Json.decodeFromString<SlideList>(input.readBytes().decodeToString())
        } catch (_: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: SlideList, output: OutputStream) {
        output.write(Json.encodeToString(value = t).encodeToByteArray())
    }
}