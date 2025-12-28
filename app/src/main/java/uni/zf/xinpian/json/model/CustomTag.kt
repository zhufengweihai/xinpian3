package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomTag (
    val categoryId: String = "",
    val title: String = "",
    @SerialName("jump_address") val jumpAddress: String = ""
)

@Serializable
data class CustomTags(
    val list: List<CustomTag> = emptyList()
)
