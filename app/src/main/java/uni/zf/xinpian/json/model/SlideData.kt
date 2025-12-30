package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SlideData(
    val id: Int = 0,
    @SerialName("pos_id") val categoryId: Int = 0,
    @SerialName("jump_id") val jumpId: Int = 0,
    val thumbnail: String = "",
    val title: String = ""
)

@Serializable
data class SlideList(val data: List<SlideData> = listOf())
