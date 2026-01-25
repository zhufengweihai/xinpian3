package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class CustomTag(
    val title: String = "",
    @SerialName("jump_address") val jumpAddress: String = ""
)

@Serializable
data class CustomTags(
    val list: List<CustomTag> = emptyList()
)
