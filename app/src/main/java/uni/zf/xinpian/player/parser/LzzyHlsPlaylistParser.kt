package uni.zf.xinpian.player.parser
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist

@UnstableApi
class LzzyHlsPlaylistParser : MyHlsPlaylistParser {
    constructor() : super()
    constructor(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ) : super(multivariantPlaylist, previousMediaPlaylist)


    override fun filterAdSegments(lines: List<String>): List<String> {
        val filteredList = mutableListOf<String>()
        var lastTs: Long? = null

        for (line in lines) {
            if (line.endsWith(".ts")) {
                val tsNumber = line.substringBeforeLast(".ts")
                    .takeLastWhile { it.isDigit() }
                    .toLongOrNull()

                if (tsNumber != null) {
                    if (lastTs != null && tsNumber > lastTs + 1) {
                        filteredList.removeAt(filteredList.lastIndex)
                    } else {
                        filteredList.add(line)
                        lastTs = tsNumber
                    }
                }
            } else {
                filteredList.add(line)
            }
        }

        return filteredList
    }
}