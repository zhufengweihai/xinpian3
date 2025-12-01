package uni.zf.xinpian.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.dcloud.uts.UTSArray
import io.dcloud.uts.UTSJSONObject
import uni.zf.xinpian.utils.toVideo
import java.io.Serializable

@Entity
class Video : Serializable {
    @PrimaryKey
    var id: String = ""
    @ColumnInfo(name = "vod_ids")
    var vodIds: List<String> = emptyList()
    var name: String = ""
    @ColumnInfo(name = "name_en")
    var nameEn: String = ""
    var status: String = ""
    @ColumnInfo(name = "vod_time")
    var vodTime: Long = 0
    var image: String = ""
    var plot: String = ""
    var genres: List<String> = emptyList()
    var actors: List<String> = emptyList()
    var directors: List<String> = emptyList()
    var writers: List<String> = emptyList()
    var areas: List<String> = emptyList()
    var language: String = ""
    var year: Int = 0
    @ColumnInfo(name = "db_id")
    var dbId: Int = 0
    @ColumnInfo(name = "db_score")
    var dbScore: String = ""
    var category: String = ""
    var hotness: Int = 0
    var lastUpdate: Long = 0

    @Ignore
    var vodList: List<Vod> = emptyList()

    fun isMovie(): Boolean = category == "M"

    fun isDuanju(): Boolean = category == "D"

    fun needUpdate(): Boolean {
        return System.currentTimeMillis() - lastUpdate > 30 * 60 * 1000
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Video

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun from(data: UTSArray<UTSJSONObject>): List<Video> {
            return data.map { it -> it.toVideo() }
        }

        fun from(data: UTSJSONObject): Video {
            return data.toVideo()
        }

        fun from(record: WatchRecord): Video {
            return Video().apply {
                id = record.videoId
                name = record.name
            }
        }

        fun from(video: DownloadVideo): Video {
            return Video().apply {
                id = video.videoId
                name = video.name
            }
        }
    }
}