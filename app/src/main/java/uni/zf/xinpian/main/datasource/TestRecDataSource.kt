package uni.zf.xinpian.main.datasource

import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.alibaba.fastjson.parser.Feature
import uni.zf.xinpian.data.model.RecData
import java.io.IOException

class TestRecDataSource : RecDataSource {
    override fun fetchRecData(context: Context): RecData? {
        try {
            context.assets.open("rec_list.json").use { `is` ->
                return JSON.parseObject(`is`, object : TypeReference<RecData?>() {
                }.type, Feature.AutoCloseSource)
            }
        } catch (e: IOException) {
            return null
        }
    }
}
