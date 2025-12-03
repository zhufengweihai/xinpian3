package uni.zf.xinpian.main

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.utils.requestUrl

class MainViewModel : ViewModel() {
    private val FENLEI_URL = "https://qqhx9o.zxbwv.com/api/term/home_fenlei"
    suspend fun requestFenlei(): List<Fenlei> {
        val dataString = requestUrl(FENLEI_URL)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseFenlei(it as Map<String, Any>) }
    }

    private fun parseFenlei(data: Map<String, Any>): Fenlei {
        return Fenlei().apply {
            this.id = data["id"].toString()
            this.name = data["name"].toString()
            this.abbr = data["title"]?.toString()
        }
    }
}