package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingItem(
    val id: Int, // 电影业务ID
    val title: String, // 电影标题
    val description: String, // 电影剧情简介
    val year: String, // 上映年份（JSON中为字符串，保留原类型）
    @SerialName("hot_sort") val hotSort: Int, // 热度排序值
    @SerialName("is_short") val isShort: Int, // 是否为短片（0=否，1=是）
    @SerialName("path") val posterPath: String, // 海报存储路径
    @SerialName("category_name") val categoryName: String, // 分类名称（如"电影"）
    val types: List<String>, // 电影类型列表（如["喜剧","动画"]）
    val actors: List<String> // 主演列表
)