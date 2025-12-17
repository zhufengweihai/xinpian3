package uni.zf.xinpian.category

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.App
import uni.zf.xinpian.data.AppConst.dyTagURL
import uni.zf.xinpian.data.AppConst.slideUrl
import uni.zf.xinpian.data.AppConst.tagsUrl
import uni.zf.xinpian.data.model.CustomTag
import uni.zf.xinpian.data.model.DyTag
import uni.zf.xinpian.data.model.SlideData
import uni.zf.xinpian.data.model.TagData
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel : ViewModel() {
    private val slideDataDao = App.INSTANCE.appDb.slideDataDao()
    private val tagDao = App.INSTANCE.appDb.customTagDao()
    private val tagDataDao = App.INSTANCE.appDb.tagDataDao()
    fun getSlideDataList(categoryId: String) = slideDataDao.getSlideDataList(categoryId)

    fun getTagList(categoryId: String) = tagDao.getTagList(categoryId)

    fun getTagDatas(categoryId: String) = tagDataDao.getTagDatas(categoryId)

    suspend fun requestSlideData(categoryId: String, context: Context) {
        val dataString = requestUrl(slideUrl.format(categoryId), createHeaders(context))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        val slideDatas = dataListString.mapNotNull { parseSlideData(it as Map<String, Any>) }
        slideDataDao.insertSlideDataList(slideDatas)
    }

    suspend fun requestCustomTags(categoryId: String, context: Context) {
        val dataString = requestUrl(tagsUrl.format(categoryId), createHeaders(context))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        val tags = dataListString.mapNotNull { parseTag(categoryId, it as Map<String, Any>) }
        tagDao.insertTagList(tags)
    }

    suspend fun requestTagDatas(categoryId: String, context: Context) {
        val dataString = requestUrl(dyTagURL.format(categoryId), createHeaders(context))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        val tagDatas = dataListString.mapNotNull { parseTagData(it as Map<String, Any>) }
        tagDataDao.updateTagDatas(categoryId, tagDatas)
    }

    private fun parseSlideData(data: Map<String, Any>): SlideData {
        return SlideData().apply {
            jumpId = data["jump_id"].toString()
            thumbnail = data["thumbnail"].toString()
            title = data["title"].toString()
        }
    }

    private fun parseTag(categoryId: String, data: Map<String, Any>): CustomTag {
        return CustomTag(
            categoryId = categoryId,
            title = data["title"].toString(),
            jumpAddress = data["jump_address"].toString()
        )
    }

    private fun parseTagData(data: Map<String, Any>): TagData {
        val id = data["id"].toString()
        return TagData(
            dyTag = DyTag().apply {
                this.id = id
                categoryId = data["category_id"].toString()
                name = data["name"].toString()
                jumpAddress = data["jump_address"].toString()
                cover = data["cover"].toString()
                coverJumpAddress = data["cover_jump_address"].toString()
            },
            videoList = (data["dataList"] as List<*>).map {
                parseVideoBrief(
                    id,
                    it as Map<String, Any>
                )
            })
    }

    private fun parseVideoBrief(dyTag: String, data: Map<String, Any>): VideoBrief {
        return VideoBrief().apply {
            id = data["id"].toString()
            score = data["score"].toString()
            definition = data["definition"] as Int
            title = data["title"].toString()
            image = data["path"].toString()
            mask = data["mask"].toString()
            dyTagId = dyTag
        }
    }
}