package uni.zf.xinpian.data.model

import androidx.room.Entity

@Entity
class CustomTag {
    var categoryId: String = ""
    var title: String = ""
    var jumpAddress: String = ""
}