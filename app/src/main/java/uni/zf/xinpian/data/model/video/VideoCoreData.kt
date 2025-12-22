package uni.zf.xinpian.data.model.video

import androidx.room.PrimaryKey


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