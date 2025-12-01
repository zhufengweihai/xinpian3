package uni.zf.xinpian.utils

import uni.zf.xinpian.data.model.DownloadVideo
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.data.model.VideoData

fun Video.mergeFromServer(newVideo: Video): Video {
    vodIds = newVideo.vodIds
    nameEn = newVideo.nameEn
    status = newVideo.status
    vodTime = newVideo.vodTime
    image = newVideo.image
    genres = newVideo.genres
    actors = newVideo.actors
    directors = newVideo.directors
    areas = newVideo.areas
    dbId = newVideo.dbId
    dbScore = newVideo.dbScore
    hotness = newVideo.hotness
    return this
}

fun Video.mergeFromVod(newVideo: Video): Video {
    vodIds = vodIds.union(vodIds).toMutableList()
    if (newVideo.vodTime> vodTime) {
        status = newVideo.status
        vodTime = newVideo.vodTime
        image = newVideo.image
    }
    if (plot.isEmpty()) plot = newVideo.plot
    val excepts = listOf("其他")
    if (genres.isEmpty() || genres == excepts) genres = newVideo.genres
    if (actors.isEmpty() || actors == listOf("其他")) actors = newVideo.actors
    if (directors.isEmpty() || directors == excepts) directors = newVideo.directors
    if (writers.isEmpty() || writers == excepts) writers = newVideo.writers
    if (areas.isEmpty() || areas == excepts) areas = newVideo.areas
    if (language.isEmpty() || language == "其他") language = newVideo.language
    if (dbId == 0) dbId = newVideo.dbId
    if (dbScore <= "0") dbScore = newVideo.dbScore
    vodList = (vodList + newVideo.vodList).associateBy { it }.values.toList()
    if (newVideo.lastUpdate > lastUpdate) lastUpdate = newVideo.lastUpdate
    return this
}

fun Video.merge(newVideo: Video): Video {
    if (newVideo.vodTime > vodTime) {
        status = newVideo.status
        vodTime = newVideo.vodTime
        image = newVideo.image
        hotness = newVideo.hotness
    }
    if (plot.isEmpty()) plot = newVideo.plot
    val excepts = listOf("其他")
    if (actors.isEmpty() || actors == excepts) actors = newVideo.actors
    if (directors.isEmpty() || directors == excepts) directors = newVideo.directors
    if (writers.isEmpty() || writers == excepts) writers = newVideo.writers
    if (language.isEmpty() || language == "其他") language = newVideo.language
    if (genres.isEmpty() || genres == excepts) genres = newVideo.genres
    if (areas.isEmpty() || areas == excepts) areas = newVideo.areas
    if (dbId == 0) dbId = newVideo.dbId
    if (dbScore <= "0") dbScore = newVideo.dbScore
    vodList += newVideo.vodList
    return this
}

fun List<Video>.mergeFromVod(): Video? {
    if (isEmpty()) return null
    return reduce { mergedVideo, newVideo ->
        mergedVideo.mergeFromVod(newVideo)
    }
}

fun VideoData.toDownloadVideo(): DownloadVideo {
    return DownloadVideo(
        videoId = video.id,
        vodIndex = this.vodIndex,
        name = video.name,
        image = video.image,
        addTime = System.currentTimeMillis()
    )
}