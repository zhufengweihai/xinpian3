package uni.zf.xinpian.source

import com.alibaba.fastjson.JSON
import uni.zf.xinpian.data.convertToEpisodes
import uni.zf.xinpian.data.model.Vod
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.utils.requestUrls
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale

abstract class VodSource(
    private val symbol: String,
    val prefUpdate: String,
    private val idUrl: String = "",
    private val pgUrl: String = "",
    private val keyNameEn: String = "vod_en"
) {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val cleanRegex = Regex("<.*?>")

    open fun getUrls(vodIds: List<String>): List<String> {
        return listOf(idUrl.format(vodIds.joinToString(",")))
    }

    open fun getPgUrls(pgStart: Int): List<String> {
        return listOf(pgUrl.format(pgStart))
    }

    abstract fun getCategory(typeId: Int, typeId1: Int): String?

    open fun getGenres(category: String, genres: List<String>): List<String> {
        if (category == "D") {
            return listOf("其他")
        }

        val genreMap = mapOf(
            "M" to Organize.M_GENRES,
            "S" to Organize.S_GENRES,
            "V" to Organize.V_GENRES,
            "A" to Organize.A_GENRES
        )

        return Organize.organizeGenres(genres, genreMap[category] ?: emptyList())
    }


    open suspend fun requestVodVideo(vodIds: List<String>, customHeaders: Pair<String, String>? = null): Video? {
        return JSON.parseObject(requestUrls(getUrls(vodIds), customHeaders))?.getJSONArray("list")?.getJSONObject(0)
            ?.let { parseVideo(it) }
    }

    open suspend fun requestVodVideo(pgStart: Int, customHeaders: Pair<String, String>? = null): List<Video>? {
        return JSON.parseObject(requestUrls(getPgUrls(pgStart), customHeaders))?.getJSONArray("list")?.mapNotNull {
            parseVideo(it as Map<String, Any>)
        }
    }

    private fun parseVideo(info: Map<String, Any>): Video? {
        val typeId = info["type_id"]?.toString()?.toIntOrNull() ?: return null
        val typeId1 = info["type_id_1"]?.toString()?.toIntOrNull() ?: 0
        val category = getCategory(typeId, typeId1) ?: return null
        val playUrls = getPlayUrl(info)
        if (playUrls.isEmpty()) return null
        val year = getYear(info)
        val vodTime = parseDate(info["vod_time"]?.toString()) ?: 0
        val name = info["vod_name"].toString().trim().replace(" ", "")
        val id = generateId(name, category, year)
        return Video().apply {
            this.id = id
            this.name = name
            nameEn = info[keyNameEn].toString()
            status = info["vod_remarks"].toString()
            this.vodTime = vodTime
            this.image = info["vod_pic"].toString()
            plot = info["vod_content"]?.let { cleanRegex.replace(it as String, "").trim() } ?: ""
            genres = getGenres(category, strip(info["vod_class"].toString()))
            actors = info["vod_actor"]?.toString()?.split(",")?.map { it.trim() }?.take(10) ?: emptyList()
            directors = info["vod_director"]?.toString()?.split(",")?.map { it.trim() }?.take(3) ?: emptyList()
            writers = info["vod_writer"]?.toString()?.split(",")?.map { it.trim() }?.take(3) ?: emptyList()
            areas = Organize.organizeAreas(strip(info["vod_area"].toString()))
            language = info["vod_lang"].toString()
            this.year = year
            this.dbId = info["vod_douban_id"]?.toString()?.toIntOrNull() ?: 0
            this.dbScore = info["vod_douban_score"]?.toString()?.takeIf { (it.toFloatOrNull() ?: 0f) > 0 } ?: ""
            this.category = category
            vodList = listOf(getVod(id, info, vodTime, playUrls))
            lastUpdate = System.currentTimeMillis()
        }
    }

    private fun getYear(info: Map<String, Any>): Int {
        return info["vod_pubdate"]?.toString()?.trim()?.take(4)
            ?.takeIf { it.length == 4 && it.all { c -> c.isDigit() } }
            ?.toInt()
            ?: info["vod_year"]?.toString()?.take(4)?.toIntOrNull()
            ?: 0
    }

    private fun getVod(videoId: String, info: Map<String, Any>, vodTime: Long, playUrl: String): Vod {
        return Vod().apply {
            vodId = "$symbol${info["vod_id"]}"
            this.videoId = videoId
            status = info["vod_remarks"].toString()
            this.vodTime = vodTime
            this.image = info["vod_pic"].toString()
            this.episodes = convertToEpisodes(playUrl)
            this.downUrl = info["vod_down_url"]?.toString() ?: ""
        }
    }

    private fun getPlayUrl(info: Map<String, Any>): String {
        return info["vod_play_url"]?.toString()?.split("$$$")?.firstOrNull {
            it.contains(".m3u8") || it.contains(".mp4")
        }?.toString() ?: ""
    }

    private fun generateId(name: String, category: String, year: Int): String {
        val combinedString = "$name$category$year"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(combinedString.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }.take(16)
    }

    private fun strip(vodClass: String): List<String> {
        return vodClass.split(Regex("[,\\s]+")).filter { it.isNotEmpty() }
    }

    private fun parseDate(dateString: String?): Long? {
        return try {
            dateString?.let { sdf.parse(it)?.time }
        } catch (e: Exception) {
            0
        }
    }
}