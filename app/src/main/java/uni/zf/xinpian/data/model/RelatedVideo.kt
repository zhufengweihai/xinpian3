package uni.zf.xinpian.data.model

import androidx.room.Entity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
@Entity(primaryKeys = ["videoId", "relatedId"])
data class RelatedVideo(
    val videoId: Int,
    val title: String,
    @SerialName("path") val imageUrl: String,
    @SerialName("id") val relatedId: Int,
    val score: String = "",
)