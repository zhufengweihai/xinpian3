import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class ShortVideoItem(
    val vid: Int, // 视频主ID
    val weight: Int, // 剧集排序权重
    @SerialName("title") val episodeTitle: String, // 剧集标题（如"第1集"）
    @SerialName("id") val episodeId: Int, // 剧集唯一ID
    @SerialName("url") val episodeUrl: String, // 剧集播放地址
    @SerialName("source_id") val sourceId: Int, // 播放源ID
    @SerialName("source_config_name") val sourceConfigName: String // 播放源名称（如"常规线路"）
)