package uni.zf.xinpian.objectbox.model

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity


@Entity
data class DyTagData (
    val name: String = "",
    @SerializedName("category_id") val categoryId: Int = 0,
    val cover: String = "",
    @SerializedName("cover_jump_address") val coverJumpAddress: String = "",
    val id: Int = 0,
    val dataList : List<TagData> = listOf()
)