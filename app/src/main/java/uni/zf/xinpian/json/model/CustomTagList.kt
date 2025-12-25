package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomTagList(val data: List<CustomTag> = listOf())