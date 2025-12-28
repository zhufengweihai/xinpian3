package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlideData(
    val id: Int = 0,
    @SerialName("pos_id") val categoryId: String = "",
    @SerialName("jump_id") val jumpId: String = "",
    val thumbnail: String = "",
    val title: String = ""
)

@Serializable
data class SlideList(val data: List<SlideData> = listOf())
