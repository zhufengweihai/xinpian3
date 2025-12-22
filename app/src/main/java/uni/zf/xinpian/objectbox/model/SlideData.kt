package uni.zf.xinpian.objectbox.model

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class SlideData(
    @Id val id: Long = 0,
    @SerializedName("pos_id") val categoryId: String = "",
    @SerializedName("jump_id") val jumpId: String = "",
    val thumbnail: String = "",
    val title: String = ""
)