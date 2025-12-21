package uni.zf.xinpian.data.model.video

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
class VideoData(
    override val id: Int = 0,
    override val score: String = "",
    @SerializedName("original_name") val originalName: String = "",
    val year: String = "",
    @SerializedName("douban_id") val doubanId: Int = 0,
    override val title: String = "",
    val description: String = "",
    @SerializedName("imdb_url") val imdbUrl: String = "",
    val changed: Long = 0,
    override val definition: Int = 0,
    val duration: Int = 0,
    @SerializedName("episode_duration") val episodeDuration: String = "",
    @SerializedName("episodes_count")val episodesCount: Int = 0,
    val finished: Int = 0,
    @SerializedName("is_look")val isLook: Int = 0,
    val shared: Int = 0,
    @SerializedName("standbytime")val standbyTime: Int = 0,
    @SerializedName("time_data")val timeData: String = "",
    @SerializedName("update_cycle")val updateCycle: String = "",
    override val thumbnail: String = "",
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
    val sourceList: List<SourceGroup> = listOf(),
    val cateId: Int = 0,
    val sourceListSource: List<SourceGroup> = listOf(),
    override val mask: String = "",
    val haveCollected: Boolean = false
) : VideoCoreData(
    id,
    thumbnail,
    title,
    score,
    definition,
    mask
)