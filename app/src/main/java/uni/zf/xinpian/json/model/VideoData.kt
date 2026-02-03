package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import uni.zf.xinpian.data.model.WatchHistory

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VideoData(
    val id: Int = 0,
    val score: String = "",
    @SerialName("original_name") val originalName: String = "",
    val year: String = "",
    @SerialName("douban_id") val doubanId: Int = 0,
    val title: String = "",
    val description: String = "",
    @SerialName("imdb_url") val imdbUrl: String = "",
    val changed: Long = 0,
    val definition: Int = 0,
    val duration: String = "",
    @SerialName("episode_duration") val episodeDuration: String = "",
    @SerialName("episodes_count")
    @Serializable(with = SafeIntSerializer::class)
    val episodesCount: Int = 0,
    val finished: Int = 0,
    @SerialName("is_look") val isLook: Int = 0,
    val shared: Int = 0,
    @SerialName("standbytime") val standbyTime: Int = 0,
    @SerialName("time_data") val timeData: String = "",
    @SerialName("update_cycle") val updateCycle: String = "",
    val thumbnail: String = "",
    val languages: List<LanguageItem> = listOf(),
    val types: List<VideoType> = listOf(),
    val countries: List<String> = listOf(),
    val persons: List<Person> = listOf(),
    val directors: List<Person> = listOf(),
    val writers: List<Person> = listOf(),
    val actors: List<Person> = listOf(),
    val area: String = "",
    val category: List<CategoryItem> = listOf(),
    val topCategory: CategoryItem = CategoryItem(0, ""),
    @SerialName("source_list_source") val sourceGroups: List<SourceGroup> = listOf(),
    val cateId: Int = 0,
    val mask: String = "",
    val haveCollected: Boolean = false
){
    fun typesString() = types.joinToString(separator = "/") { it.name }
    fun actorsString() = actors.joinToString(separator = "/") { it.name }

    fun categoryString() = category.joinToString(separator = "/") { it.title }

    fun directorsString() = directors.joinToString(separator = "/") { it.name }

    fun toWatchHistory(currentSource:Int,currentItem: Int, currentPos: Long, percent: String): WatchHistory {
        return WatchHistory(
            videoId = id,
            sourceId = sourceGroups[currentSource].id,
            title = title,
            currentItem = currentItem,
            currentPos = currentPos,
            sourceName = sourceGroups[currentSource].playList[currentItem].sourceName,
            percent = percent,
            thumbnail = thumbnail,
            mask = this.mask,
            lastWatchTime = System.currentTimeMillis()
        )
    }
}