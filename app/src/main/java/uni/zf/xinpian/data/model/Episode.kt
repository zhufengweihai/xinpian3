package uni.zf.xinpian.data.model

import java.io.Serializable

data class Episode(
    var title: String = "",
    var url: String = ""
) : Serializable{

    override fun toString(): String {
        return "$title\$$url"
    }
}