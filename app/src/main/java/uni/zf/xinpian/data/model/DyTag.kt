package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import uni.zf.xinpian.data.model.VideoBrief

@Entity
data class DyTag (
    @PrimaryKey
    var id: String = "",
    var categoryId: String = "",
    var name: String = "",
    var jumpAddress: String = "",
    var cover: String = "",
    var coverJumpAddress: String = "",
    var videoList: List<VideoBrief> = emptyList()
)