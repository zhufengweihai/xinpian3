package uni.zf.xinpian.source

const val SYMBOL_WWZY = "wwzy"
private const val URL1 = "https://ww.tyyszy5.com/api.php/provide/vod/?ac=detail&ids=%s"
private const val URL2 = "https://api.wwzy.tv/api.php/provide/vod/?ac=detail&ids=%s"
private const val PG_URL1 = "https://ww.tyyszy5.com/api.php/provide/vod/?ac=detail&pg=%s&pagesize=100"
private const val PG_URL2 = "https://api.wwzy.tv/api.php/provide/vod/?ac=detail&pg=%s&pagesize=100"
private const val PREF_UPDATE = "wwzy_last_update"

class WwzySource : VodSource(SYMBOL_WWZY, PREF_UPDATE) {

    override fun getUrls(vodIds: List<String>): List<String> {
        return listOf(URL1.format(vodIds.joinToString(",")), URL2.format(vodIds.joinToString(",")))
    }

    override fun getPgUrls(pgStart: Int): List<String> {
        return listOf(PG_URL1.format(pgStart), PG_URL2.format(pgStart))
    }

    override fun getCategory(typeId: Int, typeId1: Int): String {
        return "D"
    }

    override fun getGenres(category: String, genres: List<String>): List<String> {
        return genres
    }
}