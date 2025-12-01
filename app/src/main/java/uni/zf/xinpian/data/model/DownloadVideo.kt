package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class DownloadVideo(
    @PrimaryKey
    var videoId: String,
    var vodIndex: Int = 0,
    var name: String,
    var image: String = "",
    var addTime: Long = 0,
    var totalSize: Long = 0,
    var count: Int = 0,
    var downloading: Int = 0,
    var downloaded: Int = 0
) : Serializable