package uni.zf.xinpian.category

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.model.TagData
import uni.zf.xinpian.data.model.SlideData
import uni.zf.xinpian.data.model.Tag
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel : ViewModel() {
    private val SLIDE_URL = "https://qqhx9o.zxbwv.com/api/slide/list?pos_id=%s"
    private val CUSTOM_TAGS_URL = "https://qqhx9o.zxbwv.com/api/customTags/list?category_id=%s"
    private val BY_TAG_URL = "https://qqhx9o.zxbwv.com/api/dyTag/hand_data?category_id=%s"
    suspend fun requestSlideData(categoryId:String): List<SlideData> {
        val dataString = requestUrl(SLIDE_URL.format(categoryId))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseSlideData(it as Map<String, Any>) }
    }

    suspend fun requestCustomTags(categoryId:String): List<Tag> {
        val dataString = requestUrl(CUSTOM_TAGS_URL.format(categoryId))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseTag(it as Map<String, Any>) }
    }

    suspend fun requestTagDatas(categoryId:String): List<TagData> {
        val dataString = requestUrl(BY_TAG_URL.format(categoryId))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseTagData(it as Map<String, Any>) }
    }

    private fun parseSlideData(data: Map<String, Any>): SlideData {
        return SlideData().apply {
            jumpId = data["jump_id"].toString()
            thumbnail = data["thumbnail"].toString()
            title = data["title"].toString()
        }
    }

    private fun parseTag(data: Map<String, Any>): Tag {
        return Tag().apply {
            title = data["title"].toString()
            jumpAddress = data["jump_address"].toString()
        }
    }

    private fun parseTagData(data: Map<String, Any>): TagData {
        return TagData().apply {
            name = data["name"].toString()
            jumpAddress = data["jump_address"].toString()
            cover = data["cover"].toString()
            coverJumpAddress = data["cover_jump_address"].toString()
            videoList = (data["dataList"] as List<Map<String, Any>>).mapNotNull { parseVideoBrief(it) }
        }
    }

    private fun parseVideoBrief(data: Map<String, Any>): VideoBrief {
        return VideoBrief().apply {
            id = data["id"].toString()
            score = data["score"].toString()
            definition = data["definition"] as Int
            title = data["title"].toString()
            image = data["path"].toString()
            mask = data["mask"].toString()
        }
    }
}