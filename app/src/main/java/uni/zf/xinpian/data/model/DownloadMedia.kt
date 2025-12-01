package uni.zf.xinpian.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class DownloadMedia(
    @Embedded
    var video: DownloadVideo? = null,

    @Relation(parentColumn = "videoId", entityColumn = "videoId")
    var items: List<DownloadItem>? = null
)