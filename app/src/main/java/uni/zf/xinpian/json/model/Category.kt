package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Category(
    var id: Int = 0,
    var name: String = "",
    val pid: Int
)

@Serializable
data class CategoryList(val list: List<Category> = emptyList())