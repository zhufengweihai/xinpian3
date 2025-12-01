package uni.zf.xinpian.source

import uni.zf.xinpian.data.model.Video

const val SYMBOL_DYZY = "dyzy"
private const val ID_URL = "http://caiji.dyttzyapi.com/api.php/provide/vod/from/dyttm3u8?ac=detail&ids=%s"
private const val PG_URL = "http://caiji.dyttzyapi.com/api.php/provide/vod/from/dyttm3u8?ac=detail&pg=%s&pagesize=100"
private const val PREF_UPDATE = "dyzy_last_update"

class DyzySource : VodSource(SYMBOL_DYZY, PREF_UPDATE, ID_URL, PG_URL) {

    companion object {

        private val categoryMap = mapOf(
            "M" to setOf(5, 6, 7, 8, 9, 10, 11, 12, 20), //34
            "S" to setOf(13, 14, 15, 16, 21, 22, 23, 24),
            "A" to setOf(29, 30, 31, 32, 33),
            "V" to setOf(25, 26, 27, 28),
            "D" to setOf(36)
        )
    }

    override fun getCategory(typeId: Int, typeId1: Int): String? {
        for ((category, ids) in categoryMap) {
            if (typeId in ids) {
                return category
            }
        }
        return null
    }

    override suspend fun requestVodVideo(vodIds: List<String>, customHeaders: Pair<String, String>?): Video? {
        return super.requestVodVideo(vodIds, "authority" to "caiji.dyttzyapi.com")
    }

    override suspend fun requestVodVideo(pgStart: Int, customHeaders: Pair<String, String>?): List<Video>? {
        return super.requestVodVideo(pgStart, "authority" to "caiji.dyttzyapi.com")
    }
}