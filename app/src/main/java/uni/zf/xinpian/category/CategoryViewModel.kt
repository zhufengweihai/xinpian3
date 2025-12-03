package uni.zf.xinpian.category

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.model.Tag
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel : ViewModel() {
    private val SLIDE_URL = "https://qqhx9o.zxbwv.com/api/slide/list?pos_id=%s"
    private val CUSTOM_TAGS_URL = "https://qqhx9o.zxbwv.com/api/customTags/list?category_id=%s"
    private val BY_TAG_URL = "https://qqhx9o.zxbwv.com/api/dyTag/hand_data?category_id=%s"
    suspend fun requestSlideData(categoryId:String): List<VideoBrief> {
        val dataString = requestUrl(SLIDE_URL.format(categoryId))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseVideoNail(it as Map<String, Any>) }
    }

    suspend fun requestCustomTags(categoryId:String): List<Tag> {
        val dataString = requestUrl(CUSTOM_TAGS_URL.format(categoryId))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseTag(it as Map<String, Any>) }
    }

    suspend fun requestByTagData(categoryId:String): List<VideoBrief> {
        val dataString = requestUrl(BY_TAG_URL.format(categoryId))
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseVideoNail(it as Map<String, Any>) }
    }

    private fun parseTag(data: Map<String, Any>): Tag {
        return Tag().apply {
            title = data["title"].toString()
            jumpAddress = data["jump_address"].toString()
        }
    }

    private fun parseVideoNail(data: Map<String, Any>): VideoBrief {
        return VideoBrief().apply {
            id = data["jump_id"].toString()
            image = data["thumbnail"].toString()
            title = data["title"].toString()
        }
    }

}