package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.DyTagList
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun Context.createDyTagDataStore(id: Int) = DataStoreFactory.create(
    DyTagSerializer(),
    produceFile = { File(filesDir, "dy_tag_$id.json") }
)

class DyTagSerializer : Serializer<DyTagList> {
    override val defaultValue = DyTagList()

    override suspend fun readFrom(input: InputStream): DyTagList {
        return try {
            Json.decodeFromString<DyTagList>(input.readBytes().decodeToString())
        } catch (_: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: DyTagList, output: OutputStream) {
        output.write(Json.encodeToString(value = t).encodeToByteArray())
    }
}