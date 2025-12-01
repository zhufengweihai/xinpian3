package uni.zf.xinpian.data

import androidx.room.TypeConverter
import uni.zf.xinpian.data.model.Episode

object EpisodeConverters {
    @TypeConverter
    fun fromEpisodes(episodes: List<Episode>): String {
        return episodes.joinToString("#")
    }

    @TypeConverter
    fun toEpisodes(string: String): List<Episode> {
        return convertToEpisodes(string)
    }
}

fun convertToEpisodes(string: String): List<Episode> {
    return string.split('#')
        .mapNotNull { segment ->
            segment.split('$').takeIf { it.size >= 2 }?.let {
                Episode(it[0], it[1])
            }
        }
}