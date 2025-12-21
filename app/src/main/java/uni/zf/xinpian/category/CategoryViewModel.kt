package uni.zf.xinpian.category

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.objectbox.kotlin.equal
import io.objectbox.kotlin.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.App
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.objectbox.model.CustomTag
import uni.zf.xinpian.objectbox.ObjectBoxManager
import uni.zf.xinpian.objectbox.model.DyTagData
import uni.zf.xinpian.objectbox.model.SlideData
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel : ViewModel() {
    private val slideDataDao = App.INSTANCE.appDb.slideDataDao()
    private val tagDao = App.INSTANCE.appDb.customTagDao()
    private val tagDataDao = App.INSTANCE.appDb.tagDataDao()
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSlideDataList(categoryId: Int): Flow<List<SlideData>> {
        val slideBox = ObjectBoxManager.getBox(SlideData::class.java)
        return slideBox.query().equal(SlideData_.categoryId, categoryId).build().flow()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTagList(categoryId: Int) : Flow<List<CustomTag>>{
        val tagBox = ObjectBoxManager.getBox(CustomTag::class.java)
        return tagBox.query().equal(CustomTag_.categoryId, categoryId).build().flow()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTagDatas(categoryId: Int) : Flow<List<DyTagData>>{
        val tagDataBox = ObjectBoxManager.getBox(DyTagData::class.java)
        return tagDataBox.query().equal(DyTagData_.categoryId, categoryId).build().flow()
    }

    suspend fun requestSlideData(categoryId: String, context: Context) {
        val json = requestUrl(slideUrl.format(categoryId), createHeaders(context))
        val gson = Gson()
        val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
        val dataArray = fullJsonObject.getAsJsonArray("data")
        val slideList = dataArray.map { gson.fromJson(it, SlideData::class.java) }
        val slideBox = ObjectBoxManager.getBox(SlideData::class.java)
        slideBox.put(slideList)
    }

    suspend fun requestCustomTags(categoryId: String, context: Context) {
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

    suspend fun requestTagDatas(categoryId: String, context: Context) {
        val json = requestUrl(dyTagURL.format(categoryId), createHeaders(context))
        val gson = Gson()
        val fullJsonObject = gson.fromJson(json, JsonObject::class.java)
        val dataArray = fullJsonObject.getAsJsonArray("data")
        val dyTagDatas = dataArray.map { gson.fromJson(it, DyTagData::class.java) }
        val dyTagBox = ObjectBoxManager.getBox(DyTagData::class.java)
        dyTagBox.put(dyTagDatas)
    }
}