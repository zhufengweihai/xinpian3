package uni.zf.xinpian.recommend

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.model.VideoBrief
import uni.zf.xinpian.utils.requestUrl

class RecDataViewModel : ViewModel() {
    private val BANNER_URL = "https://otcrdv.zxbwv.com/api/dyTag/hand_data?category_id=88"
    suspend fun requestBannerData(): List<VideoBrief> {
        val dataString = requestUrl(BANNER_URL)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseBriefVideo(it as Map<String, Any>) }
    }

    private fun parseBriefVideo(data: Map<String, Any>): VideoBrief {
        return VideoBrief().apply {
            this.id = data["jump_id"].toString()
            this.image = data["thumbnail"].toString()
            this.title = data["title"].toString()
        }
    }

}