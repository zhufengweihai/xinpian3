package uni.zf.xinpian.json.model;

import kotlinx.serialization.ExperimentalSerializationApi
import java.util.List;

import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class Area(
    val area: String // 地区名称（如"香港"）
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ResCategory(
    val id: Int,
    val name: String
)

@Serializable
data class Year(
    @SerialName("year") val year: String // 年份（如"2025"）
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SearchResultItem(
    val actors: List<String>, // 演员列表（如["古天乐","梁家辉"]）
    val areas: List<Area>, // 地区（直接赋值"香港"，不再是列表）
    @SerialName("created_at") val createdAt: String, // 创建时间（ISO格式字符串）
    val description: String, // 剧情简介
    val directors: List<String>, // 导演列表
    val id: Int, // 业务ID
    @SerialName("is_show") val isShow: Int, // 是否展示（1=展示）
    val mask: String, // 格式标记（如"HD国粤双语中字"）
    @SerialName("original_name") val originalName: String, // 原始名称（如"風林火山"）
    val pinyin: String, // 全拼（如"fenglinhuoshan"）
    @SerialName("pinyin_short") val pinyinShort: String, // 拼音缩写（如"flhs"）
    @SerialName("res_categories") val resCategories: List<ResCategory>, // 资源分类列表
    val score: String, // 评分（如6.4）
    val status: Int, // 状态（1=正常）
    val thumbnail: String, // 缩略图地址
    val title: String, // 影视标题（如"风林火山"）
    @SerialName("top_category") val topCategory: ResCategory, // 顶级分类（单个对象）
    @SerialName("tvimg") val tvImgUrl: String, // TV端图片地址
    val type: Int, // 类型标识（1=电影）
    val types: List<String>, // 题材类型（如["犯罪"]）
    val years: List<Year> // 年份列表（如需简化可参考areas修改）
) {
    fun info() = "${years.joinToString("/") { it.year }}/${areas.joinToString("/") { it.area }}/${
        resCategories
            .joinToString("/") {
                it.name
            }
    }"
}