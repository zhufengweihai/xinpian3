package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName

data class WeekRankOption(
    val name: String,
    @SerialName("category_id") val categoryId: Int
)
