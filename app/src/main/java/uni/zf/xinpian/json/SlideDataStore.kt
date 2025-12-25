package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
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

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    override suspend fun readFrom(input: InputStream): SlideList {
        return json.decodeFromString(
            deserializer = SlideList.serializer(),
            string = input.readBytes().decodeToString()
        )
    }

    override suspend fun writeTo(t: SlideList, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = SlideList.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}