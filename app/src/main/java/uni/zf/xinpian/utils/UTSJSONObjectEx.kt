package uni.zf.xinpian.utils

import io.dcloud.uts.UTSArray
import io.dcloud.uts.UTSJSONObject
import uni.zf.xinpian.data.model.Video

fun UTSJSONObject.toVideo(): Video = Video().apply {
    id = getString("_id", "")
    name = getString("name", "")
    vodIds =getArray("vod_ids", UTSArray<String>()) as? List<String> ?: emptyList()
    status = getString("status", "")
    image = getString("image", "")
    dbId = getNumber("db_id", 0).toInt()
    dbScore = getString("db_score", "")
    vodTime = getNumber("vod_time", 0).toLong()
}