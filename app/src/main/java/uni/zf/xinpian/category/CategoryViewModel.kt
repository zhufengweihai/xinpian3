package uni.zf.xinpian.category

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.model.TagData
import uni.zf.xinpian.data.model.SlideData
import uni.zf.xinpian.data.model.Tag
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.utils.requestUrl

class CategoryViewModel : ViewModel() {
    private val SLIDE_URL = "https://qqhx9o.zxbwv.com/api/slide/list?pos_id=%s"
    private val CUSTOM_TAGS_URL = "https://qqhx9o.zxbwv.com/api/customTags/list?category_id=%s"
    private val BY_TAG_URL = "https://qqhx9o.zxbwv.com/api/dyTag/hand_data?category_id=%s"
    suspend fun requestSlideData(categoryId: String, context: Context): List<SlideData> {
        val customHeaders = mapOf(
            "Host" to "qqhx9o.zxbwv.com",
            "Connection" to "keep-alive",
            "sec-ch-ua-platform" to "Android",
            "timestamp" to (System.currentTimeMillis() / 1000).toString(),
            "signature" to "7228a5a21f1010fac86f1219216c637a",
            "sec-ch-ua-mobile" to "\"Chromium\";v=\"142\", \"Android WebView\";v=\"142\", \"Not_A Brand\";v=\"99\"",
            "sec-ch-ua-mobile" to "?1",
            "User-Agent" to AppData.getInstance(context).userAgent,
            "Accept" to "application/json, text/plain, */*",
            "version" to "417",
            "X-Requested-With" to "com.qihoo.jp22",
            "Sec-Fetch-Site" to "cross-site",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Dest" to "empty",
            "Accept-Encoding" to "gzip, deflate, br, zstd",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,it-IT;q=0.6,it;q=0.5"
        )
        val dataString = requestUrl(SLIDE_URL.format(categoryId), customHeaders)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseSlideData(it as Map<String, Any>) }
    }

    suspend fun requestCustomTags(categoryId: String, context: Context): List<Tag> {
        val customHeaders = mapOf(
            "Host" to "qqhx9o.zxbwv.com",
            "Connection" to "keep-alive",
            "sec-ch-ua-platform" to "Android",
            "timestamp" to (System.currentTimeMillis() / 1000).toString(),
            "signature" to "7228a5a21f1010fac86f1219216c637a",
            "sec-ch-ua-mobile" to "\"Chromium\";v=\"142\", \"Android WebView\";v=\"142\", \"Not_A Brand\";v=\"99\"",
            "sec-ch-ua-mobile" to "?1",
            "User-Agent" to AppData.getInstance(context).userAgent,
            "Accept" to "application/json, text/plain, */*",
            "version" to "417",
            "X-Requested-With" to "com.qihoo.jp22",
            "Sec-Fetch-Site" to "cross-site",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Dest" to "empty",
            "Accept-Encoding" to "gzip, deflate, br, zstd",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,it-IT;q=0.6,it;q=0.5"
        )
        val dataString = requestUrl(CUSTOM_TAGS_URL.format(categoryId), customHeaders)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseTag(it as Map<String, Any>) }
    }

    suspend fun requestTagDatas(categoryId: String, context: Context): List<TagData> {
        val customHeaders = mapOf(
            "Host" to "qqhx9o.zxbwv.com",
            "Connection" to "keep-alive",
            "sec-ch-ua-platform" to "Android",
            "timestamp" to (System.currentTimeMillis() / 1000).toString(),
            "signature" to "f381d18a32f8c447c0f5b5ffeda07bfb",
            "sec-ch-ua-mobile" to "\"Chromium\";v=\"142\", \"Android WebView\";v=\"142\", \"Not_A Brand\";v=\"99\"",
            "sec-ch-ua-mobile" to "?1",
            "User-Agent" to AppData.getInstance(context).userAgent,
            "Accept" to "application/json, text/plain, */*",
            "version" to "417",
            "X-Requested-With" to "com.qihoo.jp22",
            "Sec-Fetch-Site" to "cross-site",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Dest" to "empty",
            "Accept-Encoding" to "gzip, deflate, br, zstd",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,it-IT;q=0.6,it;q=0.5"
        )
        val dataString = requestUrl(BY_TAG_URL.format(categoryId), customHeaders)
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