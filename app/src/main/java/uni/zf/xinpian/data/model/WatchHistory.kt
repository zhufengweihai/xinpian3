package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WatchHistory(
    @PrimaryKey
    var videoId: Int,
    var sourceId: Long,
    var title: String = "",
    var currentItem: Int = 0,
    var sourceName: String = "",
    var currentPos: Long = 0,
    var percent: String = "",
    var thumbnail: String = "",
    var mask: String = "",
    var lastWatchTime: Long = 0
)