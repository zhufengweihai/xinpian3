package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class ShortVideo (
    val id: Int = 0,
    val title: String = "",
    @SerialName("episodes_count") val episodesCount: Int = 0,
    val url: String = "",
    @SerialName("cover_image") val coverImage: String = "",
    val weightAlias: String = ""
)