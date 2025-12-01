package uni.zf.xinpian.data.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.io.Serializable

data class VideoData(
    @Embedded val video: Video,
    @Relation(parentColumn = "id", entityColumn = "videoId")
    val vodList: List<Vod>,
    @Relation(parentColumn = "id", entityColumn = "videoId")
    val watchRecord: WatchRecord?
) : Serializable {
    @Ignore
    var vodIndex = 0

    init {
        vodIndex = vodList.indexOfFirst { it.vodId == watchRecord?.vodId }.takeIf { it >= 0 } ?: 0
    }

    fun currentEpisodes(): List<Episode> = vodList[vodIndex].episodes

    fun currentVodId(): String = vodList[vodIndex].vodId

    fun currentSource(): String = vodList[vodIndex].vodId.substring(0, 4)

    fun currentVod(): Vod = vodList[vodIndex]

    fun updateVodIndex(vod: Vod) {
        vodIndex = vodList.indexOf(vod)
    }

    fun updateVod(vodIndex: Int) {
        this.vodIndex = vodIndex
    }

    fun isDuanju(): Boolean = video.isDuanju()

    fun needUpdate(): Boolean = video.needUpdate()
}

