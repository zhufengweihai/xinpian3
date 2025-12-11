package uni.zf.xinpian.category

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.model.TagData
import uni.zf.xinpian.data.model.SlideData
import uni.zf.xinpian.data.model.Tag
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.dyTagURL
import uni.zf.xinpian.utils.generateSignature
import uni.zf.xinpian.utils.requestUrl
import uni.zf.xinpian.utils.slideUrl
import uni.zf.xinpian.utils.tagsUrl

class CategoryViewModel : ViewModel() {
    suspend fun requestSlideData(categoryId: String, context: Context): List<SlideData> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp, AppData.getInstance(context).secret)
        val headers = createHeaders(timestamp, signature, AppData.getInstance(context).userAgent)
        val dataString = requestUrl(slideUrl.format(categoryId), headers)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseSlideData(it as Map<String, Any>) }
    }

    suspend fun requestCustomTags(categoryId: String, context: Context): List<Tag> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp, AppData.getInstance(context).secret)
        val headers = createHeaders(timestamp, signature, AppData.getInstance(context).userAgent)
        val dataString = requestUrl(tagsUrl.format(categoryId), headers)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseTag(it as Map<String, Any>) }
    }

    suspend fun requestTagDatas(categoryId: String, context: Context): List<TagData> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp, AppData.getInstance(context).secret)
        val headers = createHeaders(timestamp, signature, AppData.getInstance(context).userAgent)
        val dataString = requestUrl(dyTagURL.format(categoryId), headers)
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