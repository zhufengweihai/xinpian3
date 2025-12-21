package uni.zf.xinpian.objectbox.model

import kotlinx.serialization.Serializable

@Serializable
data class SlideList(val data: List<SlideData> = listOf())