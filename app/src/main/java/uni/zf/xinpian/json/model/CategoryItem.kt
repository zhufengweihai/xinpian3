package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryItem(
    val id: Int,
    val title: String
)
