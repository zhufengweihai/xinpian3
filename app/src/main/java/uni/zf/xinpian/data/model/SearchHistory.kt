package uni.zf.xinpian.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    @PrimaryKey
    var keyword: String = "",
    var timestamp: Long = System.currentTimeMillis()
)