package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class RecommendItem(
    val id: Int, // 推荐项业务ID
    val title: String // 推荐项标题（如电影/视频名称）
)