package uni.zf.xinpian.json

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.CustomTags
import java.io.InputStream
import java.io.OutputStream

class CustomTagSerializer : Serializer<CustomTags> {
    override val defaultValue = CustomTags()

    override suspend fun readFrom(input: InputStream): CustomTags {
        return try {
            Json.decodeFromString<CustomTags>(input.readBytes().decodeToString())
        } catch (_: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: CustomTags, output: OutputStream) {
        output.write(Json.encodeToString(value = t).encodeToByteArray())
    }
}