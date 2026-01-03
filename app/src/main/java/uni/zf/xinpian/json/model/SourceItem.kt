package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SourceItem(
    val id: Long? = null,
    val sourceId: Long? = null,
    @SerialName("source_config_name") val sourceConfigName: String,
    @SerialName("time_data") val timeData: TimeData,
    val url: String,
    @SerialName("video_id") val videoId: Int,
    @SerialName("video_name") val videoName: String,
    val weight: String,
    @SerialName("source_name") val sourceName: String,
    val sort: String,
    @SerialName("vip_source") val vipSource: Int? = null
)