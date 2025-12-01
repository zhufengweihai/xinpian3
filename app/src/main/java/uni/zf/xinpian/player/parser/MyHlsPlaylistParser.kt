package uni.zf.xinpian.player.parser

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser
import androidx.media3.exoplayer.upstream.ParsingLoadable
import java.io.InputStream

private const val TAG_STREAM_INF = "#EXT-X-STREAM-INF"
const val TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY"

@UnstableApi
open class MyHlsPlaylistParser(private val parser: HlsPlaylistParser) : ParsingLoadable.Parser<HlsPlaylist> by parser {
    constructor() : this(HlsPlaylistParser())
    constructor(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ) : this(HlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist))

    override fun parse(uri: Uri, inputStream: InputStream): HlsPlaylist {
        val lines = inputStream.bufferedReader().readLines()
        val containsTagStreamInf = lines.any { it.contains(TAG_STREAM_INF) }
        val processedLines = if (containsTagStreamInf) lines else filterAdSegments(lines)
        val inputStreamCopy = processedLines.joinToString("\n").byteInputStream()
        return parser.parse(uri, inputStreamCopy)
    }

    open fun filterAdSegments(lines: List<String>): List<String> = lines
}