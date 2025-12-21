package uni.zf.xinpian.objectbox.model

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity


@Entity
data class CustomTag (
    val categoryId: String = "",
    val title: String = "",
    @SerializedName("jump_address") val jumpAddress: String = ""
)