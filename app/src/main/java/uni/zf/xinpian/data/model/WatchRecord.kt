package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["videoId"], foreignKeys = [ForeignKey(
        entity = Video::class,
        parentColumns = ["id"],
        childColumns = ["videoId"]
    )]
)
data class WatchRecord(
    var videoId: String,
    var vodId: String = "",
    var name: String = "",
    var currentItem: Int = 0,
    var currentEpisode: String = "",
    var currentPos: Long = 0,
    var percent: String = "",
    var image: String = "",
    var status: String = "",
    var lastWatchTime: Long = 0
)

fun fromVideo(videoData: VideoData, currentItem: Int, currentPos: Long, percent: String): WatchRecord {
    return WatchRecord(
        videoId = videoData.video.id,
        vodId = videoData.currentVodId(),
        name = videoData.video.name,
        currentItem = currentItem,
        currentPos = currentPos,
        currentEpisode = videoData.currentEpisodes()[currentItem].title,
        percent = percent,
        image = videoData.video.image,
        status = videoData.currentVod().status,
        lastWatchTime = System.currentTimeMillis()
    )
}