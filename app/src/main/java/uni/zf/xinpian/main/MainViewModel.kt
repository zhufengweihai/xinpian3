package uni.zf.xinpian.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import uni.zf.xinpian.App
import uni.zf.xinpian.common.AppData
import uni.zf.xinpian.data.AppConst
import uni.zf.xinpian.data.AppConst.imgDomainUrl
import uni.zf.xinpian.data.AppConst.initUrl
import uni.zf.xinpian.data.model.Category
import uni.zf.xinpian.utils.createHeaders
import uni.zf.xinpian.utils.requestUrl

class MainViewModel : ViewModel() {
    private val categoryDao = App.INSTANCE.appDb.categoryDao()

    fun getCategoryList() = categoryDao.getAll()

    suspend fun refreshSecret(context: Context) {
        val dataString = requestUrl(initUrl, createHeaders(context))
        val secret = JSON.parseObject(dataString).getJSONObject("data").getString("secret")
        AppData.getInstance(context).secret = secret
    }

    suspend fun refreshImgDomains(context: Context) {
        val dataString = requestUrl(imgDomainUrl, createHeaders(context))
        val imgDomainString = JSON.parseObject(dataString).getJSONObject("data").getString("imgDomain")
        val imgDomains =imgDomainString.split(",").map { "https://$it" }
        AppData.getInstance(context).imgDomains = imgDomains
    }

    suspend fun refreshCategoryList() {
        val dataString = requestUrl(AppConst.fenleiUrl)
        val dataListString = JSON.parseObject(dataString).getJSONArray("data")
        val fenleiList = dataListString.mapNotNull { parseCategory(it as Map<String, Any>) }
        categoryDao.updateAll(fenleiList)
    }

    private fun parseCategory(data: Map<String, Any>): Category {
        return Category(data["id"].toString(), data["name"].toString(), data["title"]?.toString())
    }
}