package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DyTag::class,
            parentColumns = ["id"],
            childColumns = ["dyTagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VideoBrief (
    @PrimaryKey
    var id: String = "",
    var image: String = "",
    var title: String = "",
    var score: String = "",
    var definition: Int = 0,
    var mask: String = "",
    var dyTagId: String = ""
)