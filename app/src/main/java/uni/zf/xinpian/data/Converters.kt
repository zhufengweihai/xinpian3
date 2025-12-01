package uni.zf.xinpian.data

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return java.lang.String.join("\$", list)
    }

    @JvmStatic
    @TypeConverter
    fun toStringList(string: String): List<String> {
        return listOf(*string.split("\$").dropLastWhile { it.isEmpty() }.toTypedArray())
    }
}