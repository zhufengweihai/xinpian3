package uni.zf.xinpian.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SlideData {
    @PrimaryKey
    var id: String = ""
    var categoryId: String = ""
    var jumpId: String = ""
    var thumbnail: String = ""
    var title: String = ""
}