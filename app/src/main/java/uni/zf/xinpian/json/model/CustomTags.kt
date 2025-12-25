package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomTags(val data: List<CustomTag> = listOf())