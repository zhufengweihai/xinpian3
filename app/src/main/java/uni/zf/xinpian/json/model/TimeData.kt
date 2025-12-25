package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeData(
    val totalDuration: String,
    val titlesDuration: String,
    val trailerDuration: String
)