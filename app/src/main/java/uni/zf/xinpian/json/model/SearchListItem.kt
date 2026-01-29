package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SearchListItem(
    val title: String, // 视频/影视标题
    val year: String, // 上映/发行年份（字符串类型，如"2026"）
    val score: String, // 评分（字符串类型，如"6.0"、"5.8"）
    @SerialName("changed") val changedTime: Long, // 数据更新时间戳（秒级）
    val id: Int, // 业务ID
    val thumbnail: String, // 缩略图地址
    @SerialName("tvimg") val tvImgUrl: String, // TV端图片地址
    val mask: String, // 标记信息（如"第1集"、"20260106"）
    @SerialName("categroyies") val categories: List<String>, // 分类列表（注意原字段拼写是categroyies，非categories）
    val actors: List<String>, // 演员列表（支持空数组）
    val area: String // 地区（支持空字符串）
) {
    fun categoryString() = categories.joinToString("/") { it }
    fun actorsString() = actors.joinToString("/") { it }
}