package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicDetail(
    val title: String, // 专题标题
    val content: String, // 专题描述
    @SerialName("cover") val coverUrl: String, // 专题封面图URL
    @SerialName("app_cover") val appCoverUrl: String, // APP端封面图URL
    @SerialName("video") val videoList: List<TagData>, // 专题下的视频列表
    @SerialName("id") val topicId: Int, // 专题ID
    @SerialName("tvimg") val tvImgUrl: String // TV端封面图URL
)
