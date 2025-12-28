package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DyTag(
    val name: String = "",
    @SerialName("category_id") val categoryId: Int = 0,
    val cover: String = "",
    @SerialName("cover_jump_address") val coverVideoId: String = "",
    val id: Int = 0,
    @SerialName("data") val dataList: List<TagData> = listOf()
)

@Serializable
data class TagData(
    val id: Long = 0,
    val title: String = "",
    val score: String = "",
    val finished: Int = 0,
    val definition: Int = 0,
    val path: String = "",
    val mask: String = ""
)

@Serializable
data class DyTagList(
    val list: List<DyTag> = emptyList()
)
