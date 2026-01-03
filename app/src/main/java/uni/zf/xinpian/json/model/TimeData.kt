package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeData(
    @SerialName("total_duration") val totalDuration: String,
    @SerialName("titles_duration") val titlesDuration: String,
    @SerialName("trailer_duration") val trailerDuration: String
)