package uni.zf.xinpian.objectbox.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class TagData (
    @Id val id: Long = 0,
    val title: String = "",
    val score: String = "",
    val definition: Int = 0,
    val path: String = "",
    val mask: String = "",
    val dyTagId: Int = 0
)