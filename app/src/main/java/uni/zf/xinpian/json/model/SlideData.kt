package uni.zf.xinpian.json.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SlideData(
    val id: Int = 0,
    @SerializedName("pos_id") val categoryId: String = "",
    @SerializedName("jump_id") val jumpId: String = "",
    val thumbnail: String = "",
    val title: String = ""
)