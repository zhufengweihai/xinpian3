package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.CustomTagList
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun Context.createCustomTagDataStore(id: Int) = DataStoreFactory.create(
    CustomTagSerializer(),
    produceFile = { File(filesDir, "custom_tag_$id.json") }
)

class CustomTagSerializer : Serializer<CustomTagList> {
    override val defaultValue = CustomTagList()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    override suspend fun readFrom(input: InputStream): CustomTagList {
        return json.decodeFromString(
            deserializer = CustomTagList.serializer(),
            string = input.readBytes().decodeToString()
        )
    }

    override suspend fun writeTo(t: CustomTagList, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = CustomTagList.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}