package uni.zf.xinpian.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TagData(
    @Embedded var dyTag: DyTag,
    @Relation(parentColumn = "id", entityColumn = "dyTagId")
    var videoList: List<VideoBrief>
)