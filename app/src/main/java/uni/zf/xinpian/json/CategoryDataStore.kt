package uni.zf.xinpian.json

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uni.zf.xinpian.json.model.CategoryList
import java.io.InputStream
import java.io.OutputStream

val Context.categoryDataStore: DataStore<CategoryList> by dataStore(
    fileName = "category.json",
    serializer = CategorySerializer(),
)

class CategorySerializer : Serializer<CategoryList> {
    override val defaultValue = CategoryList()

    override suspend fun readFrom(input: InputStream): CategoryList {
        return try {
            Json.decodeFromString<CategoryList>(input.readBytes().decodeToString())
        } catch (_: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: CategoryList, output: OutputStream) {
        output.write(Json.encodeToString(value = t).encodeToByteArray())
    }
}