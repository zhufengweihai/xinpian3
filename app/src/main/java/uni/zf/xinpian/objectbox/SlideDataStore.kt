package uni.zf.xinpian.objectbox

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import uni.zf.xinpian.objectbox.model.SlideList
import java.io.InputStream
import java.io.OutputStream

val Context.slideDataStore: DataStore<SlideList> by dataStore(
    fileName = "slide_list.json",
    serializer = SlideListSerializer()
)

class SlideListSerializer() : Serializer<SlideList> {
    override val defaultValue: SlideList = SlideList()
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

class SlideListStoreManager(private val context: Context) {
    val slideDataFlow: Flow<SlideList> = context.slideDataStore.data
        .catch { _ -> emit(SlideList()) }
        .map { it }

    suspend fun saveUserInfo(userInfo: SlideList) {
        context.slideDataStore.updateData { userInfo }
    }

    suspend fun clearUserInfo() {
        context.slideDataStore.updateData { SlideList() }
    }
}