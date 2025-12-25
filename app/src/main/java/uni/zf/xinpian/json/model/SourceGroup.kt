package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceGroup(
    val id: Long? = null, // 有些是 0 或缺失，用 Long? 兼容
    val name: String,
    val sourceList: List<SourceItem>,
    val sourceKey: String? = null,
    val vodId: Int? = null,
    val status: Int? = null,
    val type: Int? = null,
    val doubanId: Int? = null,
    val vipSource: Int? = null // 可选字段
)