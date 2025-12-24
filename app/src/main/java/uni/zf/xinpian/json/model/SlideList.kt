package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
data class SlideList(val data: List<SlideData> = listOf())