package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomTag (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var categoryId: String = "",
    var title: String = "",
    var jumpAddress: String = ""
)