package uni.zf.xinpian.json.model

import kotlinx.serialization.Serializable

@Serializable
class TagData (
    val id: Long = 0,
    val title: String = "",
    val score: String = "",
    val definition: Int = 0,
    val path: String = "",
    val mask: String = "",
    val dyTagId: Int = 0
)