package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceItem(
    val id: Long? = null,
    val sourceId: Long? = null,
    val sourceConfigName: String,
    val timeData: TimeData,
    val url: String,
    val videoId: Int,
    val videoName: String,
    val weight: String,
    val sourceName: String,
    val sort: String,
    val vipSource: Int? = null // 可选
)