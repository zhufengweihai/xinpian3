package uni.zf.xinpian.source

const val SYMBOL_LZZY = "lzzy"
private const val ID_URL = "https://cj.lziapi.com/api.php/provide/vod?ac=detail&ids=%s"
private const val PG_URL = "https://cj.lziapi.com/api.php/provide/vod?ac=detail&pg=%s&pagesize=100"
private const val PREF_UPDATE = "lzzy_last_update"

class LzzySource : VodSource(SYMBOL_LZZY, PREF_UPDATE, ID_URL, PG_URL) {

    companion object {
        private val typeId1Map = mapOf(
            1 to "M",
            2 to "S",
            3 to "A",
            4 to "V"
        )

        private val typeIdMap = mapOf(
            "M" to setOf(6, 7, 8, 9, 10, 11, 12, 20), //34,
            "S" to setOf(13, 14, 15, 16, 21, 22, 23, 24),
            "A" to setOf(25, 26, 27, 28),
            "V" to setOf(29, 30, 31, 32, 33)
        )
    }

    override fun getCategory(typeId: Int, typeId1: Int): String? {
        if (typeId == 45) {
            return null
        }
        if (typeId == 46) {
            return "D"
        }

        typeId1Map[typeId1]?.let {
            return it
        }

        for ((category, ids) in typeIdMap) {
            if (typeId in ids) {
                return category
            }
        }
        return null
    }
}