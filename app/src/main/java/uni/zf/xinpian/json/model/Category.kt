package uni.zf.xinpian.json.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Category(
    var id: Int = 0,
    var name: String = ""
) : Parcelable

@Serializable
data class CategoryList(val list: List<Category> = emptyList())