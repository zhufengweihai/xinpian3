package uni.zf.xinpian.category

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.data.model.Category
import uni.zf.xinpian.json.createCustomTagDataStore
import uni.zf.xinpian.json.createSlideDataStore
import uni.zf.xinpian.json.model.SlideList
import uni.zf.xinpian.objectbox.model.DyTag
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val category: Category = savedStateHandle.get<Category>(arg_category) ?: Category()
    private val slideDataStore = getApplication<Application>().createSlideDataStore(category.id)
    private val customTagDataStore = getApplication<Application>().createCustomTagDataStore(category.id)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSlideList(): Flow<SlideList> = slideDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCustomTagList(categoryId: Int) = customTagDataStore.data

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTagDatas(categoryId: Int) : Flow<List<DyTag>>{
        val tagDataBox = ObjectBoxManager.getBox(DyTag::class.java)
        return tagDataBox.query().equal(DyTag_.categoryId, categoryId).build().flow()
    }

    suspend fun requestSlideData(categoryId: Int, context: Context) {
        val json = requestUrl(slideUrl.format(categoryId), createHeaders(context))
        val gson = Gson()
        val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
        val dataArray = fullJsonObject.getAsJsonArray("data")
        val slideList = dataArray.map { gson.fromJson(it, SlideData::class.java) }
        val slideBox = ObjectBoxManager.getBox(SlideData::class.java)
        slideBox.put(slideList)
    }

    suspend fun requestCustomTags(categoryId: Int, context: Context) {
        val json = requestUrl(tagsUrl.format(categoryId), createHeaders(context))
        val gson = Gson()
        val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
        val dataArray = fullJsonObject.getAsJsonArray("data")
        val customTags = dataArray.map {
            (it as JsonObject).addProperty("categoryId", categoryId)
            gson.fromJson(it, CustomTag::class.java)
        }
        val tagsBox = ObjectBoxManager.getBox(CustomTag::class.java)
        tagsBox.put(customTags)
    }

    suspend fun requestTagDatas(categoryId: Int, context: Context) {
        val json = requestUrl(dyTagURL.format(categoryId), createHeaders(context))
        val gson = Gson()
        val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
        val dataArray = fullJsonObject.getAsJsonArray("data")
        val dyTagDatas = dataArray.map { gson.fromJson(it, DyTag::class.java) }
        val dyTagBox = ObjectBoxManager.getBox(DyTag::class.java)
        dyTagBox.put(dyTagDatas)
    }
}