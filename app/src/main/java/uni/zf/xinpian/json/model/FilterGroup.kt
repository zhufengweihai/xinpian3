package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class FilterGroup(
    val key: String, // 分组标识（type/area/year/sort）
    @SerialName("data") val options: List<FilterOption> // 该分组下的具体筛选项
)

/**
 * 单个筛选项（如"剧情"/"国产"/"2026"/"最热"等）
 */
@Serializable
data class FilterOption(
    val id: String, // 选项ID（支持空字符串、数字字符串、英文标识如"hot"）
    val name: String // 选项名称（如"全部"/"剧情"/"国产"）
)