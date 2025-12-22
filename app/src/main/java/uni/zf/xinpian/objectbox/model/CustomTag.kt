package uni.zf.xinpian.objectbox.model

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@Entity
data class CustomTag (
    @Id val id: Long = 0,
    val categoryId: String = "",
    val title: String = "",
    @SerializedName("jump_address") val jumpAddress: String = ""
)