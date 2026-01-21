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
    var currentEpisode: String = "",
    var currentPos: Long = 0,
    var percent: String = "",
    var image: String = "",
    var status: String = "",
    var lastWatchTime: Long = 0
)