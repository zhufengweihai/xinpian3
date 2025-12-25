package uni.zf.xinpian.json.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DyTag (
    val name: String = "",
    @SerializedName("category_id") val categoryId: Int = 0,
    val cover: String = "",
    @SerializedName("cover_jump_address") val coverJumpAddress: String = "",
    val dataList : List<TagData> = listOf()
)