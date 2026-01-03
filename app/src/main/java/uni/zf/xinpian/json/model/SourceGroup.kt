package uni.zf.xinpian.json.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SourceGroup(
    val id: Long = 0, // 有些是 0 或缺失，用 Long? 兼容
    val name: String,
    @SerialName("source_list") val playList: List<SourceItem>,
    val sourceKey: String? = null,
    val vodId: Int? = null,
    val status: Int? = null,
    val type: Int? = null,
    val doubanId: Int? = null,
    val vipSource: Int? = null // 可选字段
)