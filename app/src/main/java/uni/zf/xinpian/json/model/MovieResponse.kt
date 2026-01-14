package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

/**
 * 最外层响应类（可选，方便解析完整JSON）
 */
@Serializable
data class MovieResponse(
    val totalCount: Int,
    val totalPage: Int,
    val page: Int,
    val pageSize: Int,
    val pageCount: Int,
    val code: Int,
    val msg: String,
    val data: List<YearMovieData>
)

/**
 * 年份维度的电影数据（data数组的元素）
 */
@Serializable
data class YearMovieData(
    @SerialName("name") val year: Int,
    @SerialName("sub") val dateMovieList: List<DateMovieGroup>
)

/**
 * 日期维度的电影分组（如"04月03日"分组）
 */
@Serializable
data class DateMovieGroup(
    @SerialName("sub_name") val date: String,
    @SerialName("sub") val movieList: List<MovieDetail>
)

/**
 * 电影详情（最内层的电影信息）
 */
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class MovieDetail(
    @SerialName("want_num") val wantNum: String, // 想看人数文本（如"114人想看"）
    val title: String, // 电影标题
    @SerialName("main_img") val mainImage: String, // 主海报URL
    @SerialName("online_date") val onlineDate: String, // 上映日期文本
    val content: String, // 剧情简介
    @SerialName("image_url") val imageUrl: String, // 图片URL（同main_img）
    val duration: Int, // 时长（秒）
    @SerialName("video_path") val videoPath: String, // 视频路径
    @SerialName("douban_id") val doubanId: Long, // 豆瓣ID
    @SerialName("want_to_see") val wantToSee: Int, // 想看人数（纯数字）
    @SerialName("time")
    @Serializable(with = TimeStampSerializer::class)
    val time: Long,
    @SerialName("path") val posterPath: String // 海报存储路径
)