package uni.zf.xinpian.data.model.video

import androidx.room.Entity

@Entity
data class VideoData (
    val id: Int,
    val score: String,
    val originalName: String,
    val year: String,
    val doubanId: Int,
    val title: String,
    val description: String,
    val imdbUrl: String,
    val changed: Long,
    val definition: Int,
    val duration: Int,
    val episodeDuration: String,
    val episodesCount: Int,
    val finished: Int,
    val isLook: Int,
    val needGoldVip: Int,
    val shared: Int,
    val standbyTime: Int,
    val timeData: String,
    val updateCycle: String,
    val thumbnail: String,
    val tvimg: String,
    val languages: List<String>,
    val types: List<VideoType>,
    val directors: List<Person>,
    val writers: List<Person>,
    val actors: List<Person>,
    val area: String,
    val category: List<CategoryItem>,
    val topCategory: CategoryItem,
    val cateId: Int,
    val sourceListSource: List<SourceGroup>,
    val mask: String,
    val haveCollected: Boolean
)