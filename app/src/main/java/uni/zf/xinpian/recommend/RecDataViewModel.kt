package uni.zf.xinpian.recommend

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.model.BriefVideo
import uni.zf.xinpian.utils.requestUrl

class RecDataViewModel : ViewModel() {
    private val BANNER_URL = "https://otcrdv.zxbwv.com/api/dyTag/hand_data?category_id=88"
    suspend fun requestBannerData(): List<BriefVideo> {
        val dataString = requestUrl(BANNER_URL)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseBriefVideo(it as Map<String, Any>) }
    }

    private fun parseBriefVideo(data: Map<String, Any>): BriefVideo {
        return BriefVideo().apply {
            this.id = data["jump_id"].toString()
            this.thumbnail = data["thumbnail"].toString()
            this.title = data["title"].toString()
        }
    }

}