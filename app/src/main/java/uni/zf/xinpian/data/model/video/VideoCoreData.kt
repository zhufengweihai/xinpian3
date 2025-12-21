package uni.zf.xinpian.data.model.video

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uni.zf.xinpian.data.model.DyTag

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DyTag::class,
            parentColumns = ["id"],
            childColumns = ["dyTagId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ]
)
open class VideoCoreData (
    @PrimaryKey
    open val id: Int = 0,
    open val thumbnail: String = "",
    open val title: String = "",
    open val score: String = "",
    open val definition: Int = 0,
    open val mask: String = "",
    val dyTagId: String = ""
)