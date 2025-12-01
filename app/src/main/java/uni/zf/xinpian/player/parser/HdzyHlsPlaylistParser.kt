package uni.zf.xinpian.player.parser

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist

@UnstableApi
class HdzyHlsPlaylistParser : MyHlsPlaylistParser {
    constructor() : super()
    constructor(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ) : super(multivariantPlaylist, previousMediaPlaylist)


    override fun filterAdSegments(lines: List<String>): List<String> {
        val filteredList = mutableListOf<String>()
        var isAdSegment = false

        lines.forEach {
            when {
                it == TAG_DISCONTINUITY || it == "#EXT-X-ENDLIST" -> {
                    isAdSegment = false
                    filteredList.add(it)
                }

                isAdSegment -> Unit

                it == "#EXTINF:6.666667," && filteredList.lastOrNull() == TAG_DISCONTINUITY -> {
                    isAdSegment = true
                    filteredList.removeAt(filteredList.lastIndex)
                }

                it.endsWith(".ts") -> filteredList.add(it)

                else -> filteredList.add(it)
            }
        }

        return filteredList
    }
}