package uni.zf.xinpian.play.parse

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParserFactory
import androidx.media3.exoplayer.upstream.ParsingLoadable
const val KEY_DYZY = "back_source_list_dyzy"
const val KEY_BFZY = "back_source_list_mdzy"
const val KEY_FFZY = "back_source_list_ffzy"
const val KEY_HDZY = "back_source_list_hdzy"
const val KEY_LZZY = "back_source_list_lzzy"

@UnstableApi
class CustomHlsParserFactory : HlsPlaylistParserFactory {
    @Volatile
    private var parserTag: String = ""

    fun switchParser(parserTag: String) {
        synchronized(this) {
            this.parserTag = parserTag
        }
    }

    override fun createPlaylistParser(): ParsingLoadable.Parser<HlsPlaylist> {
        return when (parserTag) {
            KEY_DYZY -> DyttHlsPlaylistParser()
            KEY_BFZY -> BfzyHlsPlaylistParser()
            KEY_FFZY -> FfzyHlsPlaylistParser()
            KEY_HDZY -> HdzyHlsPlaylistParser()
            KEY_LZZY -> DyttHlsPlaylistParser()
            else -> CustomHlsPlaylistParser()
        }
    }

    override fun createPlaylistParser(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ): ParsingLoadable.Parser<HlsPlaylist> {
        return when (parserTag) {
            KEY_DYZY -> DyttHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            KEY_BFZY -> BfzyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            KEY_FFZY -> FfzyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            KEY_HDZY -> HdzyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            KEY_LZZY -> DyttHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            else -> CustomHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
        }
    }
}