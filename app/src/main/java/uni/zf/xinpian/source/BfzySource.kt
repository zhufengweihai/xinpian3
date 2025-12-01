package uni.zf.xinpian.source

import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.utils.requestUrls

const val SYMBOL_BFZY = "bfzy"
private const val ID_URL = "http://by.bfzyapi.com/api.php/provide/vod?ac=detail&ids=%s"
private const val PG_URL = "http://by.bfzyapi.com/api.php/provide/vod?ac=detail&pg=%s&pagesize=100"
private const val PREF_UPDATE = "bfzy_last_update"

class BfzySource : VodSource(SYMBOL_BFZY, PREF_UPDATE, ID_URL, PG_URL) {

    companion object {
        private val typeId1Map = mapOf(
            20 to "M",
            30 to "S",
            39 to "A",
            45 to "V",
            58 to "D"
        )

        private val typeIdRangesMap = mapOf(
            "M" to 20..28,
            "S" to 30..38,
            "A" to 39..44,
            "V" to 45..49,
            "D" to 65..72
        )
    }

    override fun getCategory(typeId: Int, typeId1: Int): String? {
        typeId1Map[typeId1]?.let { return it }
        if (typeId == 50) {
            return "M"
        }
        for ((category, idRange) in typeIdRangesMap) {
            if (typeId in idRange) {
                return category
            }
        }
        return null
    }

    override suspend fun requestVodVideo(vodIds: List<String>, customHeaders: Pair<String, String>?): Video? {
        return super.requestVodVideo(vodIds, "Host" to "by.bfzyapi.com")
    }

    override suspend fun requestVodVideo(pgStart: Int, customHeaders: Pair<String, String>?): List<Video>? {
        return super.requestVodVideo(pgStart, "Host" to "by.bfzyapi.com")
    }
}