package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import uni.zf.xinpian.data.EpisodeConverters
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = Video::class,
        parentColumns = ["id"],
        childColumns = ["videoId"]
    )], indices = [Index(value = ["videoId"])]
)
class Vod : Serializable {
    @PrimaryKey
    var vodId: String = ""
    var videoId: String = ""
    var status: String = ""
    var vodTime: Long = 0
    var image: String = ""
    @TypeConverters(EpisodeConverters::class)
    var episodes: List<Episode> = emptyList()
    var downUrl: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vod

        return vodId == other.vodId
    }

    override fun hashCode(): Int {
        return vodId.hashCode()
    }
}