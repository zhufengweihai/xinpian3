package uni.zf.xinpian.recommend

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.UNI69B4A3A.uni.zf.xinpian.data.model.video.VideoCoreData
import uni.zf.xinpian.utils.requestUrl

class RecDataViewModel : ViewModel() {
    private val BANNER_URL = "https://otcrdv.zxbwv.com/api/dyTag/hand_data?category_id=88"
    suspend fun requestBannerData(): List<VideoCoreData> {
        val dataString = requestUrl(BANNER_URL)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseBriefVideo(it as Map<String, Any>) }
    }

    private fun parseBriefVideo(data: Map<String, Any>): VideoCoreData {
        return VideoCoreData().apply {
            this.id = data["jump_id"].toString()
            this.image = data["thumbnail"].toString()
            this.title = data["title"].toString()
        }
    }

}