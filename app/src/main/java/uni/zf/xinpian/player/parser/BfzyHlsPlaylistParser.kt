package uni.zf.xinpian.player.parser

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist

@UnstableApi
class BfzyHlsPlaylistParser : MyHlsPlaylistParser {
    constructor() : super()
    constructor(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ) : super(multivariantPlaylist, previousMediaPlaylist)

    override fun filterAdSegments(lines: List<String>): List<String> {
        val toRemove = mutableSetOf<Int>()
        var lastDiscontinuityIndex: Int? = null
        var isAd = false

        for ((index, line) in lines.withIndex()) {
            if (line.startsWith("/video/adjump") || line.endsWith("_z5q.ts")) {
                isAd = true
            } else if (line == TAG_DISCONTINUITY) {
                if (lastDiscontinuityIndex != null && isAd) {
                    for (i in lastDiscontinuityIndex + 1 until index) {
                        toRemove.add(i)
                    }
                    isAd = false
                }
                lastDiscontinuityIndex = index
            }
        }

        return lines.filterIndexed { index, _ -> index !in toRemove }
    }
}