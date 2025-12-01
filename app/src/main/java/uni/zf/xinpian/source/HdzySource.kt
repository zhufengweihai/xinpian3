package uni.zf.xinpian.source

const val SYMBOL_HDZY = "hdzy"
private const val ID_URL = "https://api.yzzy-api.com/inc/apijson.php?ac=detail&ids=%s"
private const val PG_URL = "https://api.yzzy-api.com/inc/apijson.php?ac=detail&pg=%s"
private const val PREF_UPDATE = "hdzy_last_update"

class HdzySource : VodSource(SYMBOL_HDZY, PREF_UPDATE, ID_URL, PG_URL,"vod_enname") {

    companion object {

        private val typeIdMap = mapOf(
            "M" to setOf(5, 6, 7, 8, 9, 10, 11, 20, 41), //,61
            "S" to setOf(12, 13, 14, 15, 16, 17, 18, 54),
            "A" to setOf(66, 67, 68, 69, 70),
            "V" to setOf(62, 63, 64, 65),
            "D" to setOf(83),
        )
    }

    override fun getCategory(typeId: Int, typeId1: Int): String? {
        for ((category, ids) in typeIdMap) {
            if (typeId in ids) {
                return category
            }
        }
        return null
    }
}