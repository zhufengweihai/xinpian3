package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeekRankOption(
    val name: String,
    @SerialName("category_id") val categoryId: Int
)
