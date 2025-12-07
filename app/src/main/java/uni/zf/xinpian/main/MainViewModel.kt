package uni.zf.xinpian.main

import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.Fenlei
import uni.zf.xinpian.utils.requestUrl

class MainViewModel : ViewModel() {
    private val IMAGE_DOMAIN_URL = "https://qqhx9o.zxbwv.com/api/resourceDomainConfig"
    private val FENLEI_URL = "https://qqhx9o.zxbwv.com/api/term/home_fenlei"
    suspend fun requestFenlei(): List<Fenlei> {
        val dataString = requestUrl(FENLEI_URL)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        return dataListString.mapNotNull { parseFenlei(it as Map<String, Any>) }
    }

    suspend fun requestImgDomains() {
        val dataString = requestUrl(IMAGE_DOMAIN_URL)
        val imgDomainString = JSON.parseObject(dataString).getJSONObject("data").getString("imgDomain")
        //App.INSTANCE.setImgDomains(imgDomainString.split(","))
    }

    private fun parseFenlei(data: Map<String, Any>): Fenlei {
        return Fenlei(data["id"].toString(), data["name"].toString(), data["title"]?.toString())
    }
}