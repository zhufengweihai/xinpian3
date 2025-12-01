package uni.zf.xinpian.player.parser

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParserFactory
import androidx.media3.exoplayer.upstream.ParsingLoadable
import uni.zf.xinpian.source.SYMBOL_BFZY
import uni.zf.xinpian.source.SYMBOL_FFZY
import uni.zf.xinpian.source.SYMBOL_HDZY
import uni.zf.xinpian.source.SYMBOL_LZZY

@UnstableApi
class MyHlsParserFactory : HlsPlaylistParserFactory {
    @Volatile
    private var parserTag: String = ""

    fun switchParser(parserTag: String) {
        synchronized(this) {
            this.parserTag = parserTag
        }
    }

    override fun createPlaylistParser(): ParsingLoadable.Parser<HlsPlaylist> {
        return when (parserTag) {
            SYMBOL_BFZY -> BfzyHlsPlaylistParser()
            SYMBOL_FFZY -> FfzyHlsPlaylistParser()
            SYMBOL_HDZY -> HdzyHlsPlaylistParser()
            SYMBOL_LZZY -> LzzyHlsPlaylistParser()
            else -> MyHlsPlaylistParser()
        }
    }

    override fun createPlaylistParser(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ): ParsingLoadable.Parser<HlsPlaylist> {
        return when (parserTag) {
            SYMBOL_FFZY -> FfzyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            SYMBOL_HDZY -> HdzyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            SYMBOL_LZZY -> LzzyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
            else -> MyHlsPlaylistParser(multivariantPlaylist, previousMediaPlaylist)
        }
    }
}