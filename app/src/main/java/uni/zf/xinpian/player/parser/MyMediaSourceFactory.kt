package uni.zf.xinpian.player.parser

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy

@UnstableApi
class MyMediaSourceFactory(private val dataSourceFactory: DataSource.Factory) : MediaSource.Factory {
    private val myHlsParserFactory = MyHlsParserFactory()

    fun switchParser(parserTag: String) {
        myHlsParserFactory.switchParser(parserTag)
    }

    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val uri = mediaItem.localConfiguration?.uri ?: throw IllegalArgumentException("MediaItem URI is null")
        return if (uri.toString().endsWith(".m3u8")) {
            HlsMediaSource.Factory(dataSourceFactory)
                .setPlaylistParserFactory(myHlsParserFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }
    }

    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider): MediaSource.Factory {
        return this
    }

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy): MediaSource.Factory {
        return this
    }

    override fun getSupportedTypes(): IntArray {
        return intArrayOf(
            C.CONTENT_TYPE_HLS,
            C.CONTENT_TYPE_OTHER
        )
    }
}