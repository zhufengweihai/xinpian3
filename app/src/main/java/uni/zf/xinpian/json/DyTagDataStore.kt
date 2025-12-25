package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.DyTag
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun Context.createDyTagDataStore(id: Int) = DataStoreFactory.create(
    DyTagSerializer(),
    produceFile = { File(filesDir, "dy_tag_$id.json") }
)

class DyTagSerializer : Serializer<DyTag> {
    override val defaultValue = DyTag()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    override suspend fun readFrom(input: InputStream): DyTag {
        return json.decodeFromString(
            deserializer = DyTag.serializer(),
            string = input.readBytes().decodeToString()
        )
    }

    override suspend fun writeTo(t: DyTag, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = DyTag.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}