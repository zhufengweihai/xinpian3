package uni.zf.xinpian.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Category(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var abbr: String?
) : Parcelable