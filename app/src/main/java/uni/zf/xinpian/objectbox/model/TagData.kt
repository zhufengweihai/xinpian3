package uni.zf.xinpian.objectbox.model

import io.objectbox.annotation.Entity

@Entity
class TagData (
    val id: Int = 0,
    val title: String = "",
    val score: String = "",
    val definition: Int = 0,
    val path: String = "",
    val mask: String = "",
    val dyTagId: Int = 0
)