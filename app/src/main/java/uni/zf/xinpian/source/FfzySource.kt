package uni.zf.xinpian.source

const val SYMBOL_FFZY = "ffzy"
private const val ID_URL = "https://api.ffzyapi.com/api.php/provide/vod/from/ffm3u8?ac=detail&ids=%s"
private const val PG_URL = "https://api.ffzyapi.com/api.php/provide/vod/from/ffm3u8?ac=detail&pg=%s&pagesize=100"
private const val PREF_UPDATE = "ffzy_last_update"

class FfzySource : VodSource(SYMBOL_FFZY, PREF_UPDATE, ID_URL, PG_URL) {

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
}