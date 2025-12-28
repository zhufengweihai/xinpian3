package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.SlideList
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun Context.createSlideDataStore(id: Int) = DataStoreFactory.create(
    SlideListSerializer(),
    produceFile = { File(filesDir, "slide_list_$id.json") }
)

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