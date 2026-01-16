package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Special(
    val title: String, // 专题标题（如"杨门女将系列电影专辑"）
    @SerialName("cover") val coverUrl: String, // 专题封面图URL
    @SerialName("app_cover") val appCoverUrl: String, // APP端专用封面图URL
    @SerialName("id") val topicId: Int, // 专题唯一标识ID
    @SerialName("tvimg") val tvImgUrl: String // TV端封面图URL（和cover值一致，保留字段）
)