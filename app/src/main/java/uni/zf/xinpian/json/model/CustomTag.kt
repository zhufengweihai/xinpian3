package uni.zf.xinpian.json.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class CustomTag (
    val categoryId: String = "",
    val title: String = "",
    @SerializedName("jump_address") val jumpAddress: String = ""
)