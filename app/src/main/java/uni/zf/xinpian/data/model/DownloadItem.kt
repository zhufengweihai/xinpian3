package uni.zf.xinpian.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["vodId", "index"])
data class DownloadItem(
    var vodId: String = "",
    var index: Int = 0,
    var url: String = "",
    var videoId: String = "",
    var episode: String = "",
    var state: Int = 0,
    var bytesDownloaded: Long = 0,
    var percentDownloaded: Float = 0f
) {
    companion object {
        fun fromVideo(video: Video, vod: Vod, itemIndex: Int): DownloadItem {
            val episode = vod.episodes[itemIndex]
            return DownloadItem(
                vodId = vod.vodId,
                index = itemIndex,
                url = episode.url,
                videoId = video.id,
                episode = episode.title
            )
        }
    }
}