package uni.zf.xinpian.play.parse

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import uni.zf.xinpian.player.parser.MyHlsParserFactory

@UnstableApi
class CustomMediaSourceFactory(private val dataSourceFactory: DataSource.Factory) : MediaSource.Factory {
    private val myHlsParserFactory = MyHlsParserFactory()

    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        mediaItem.mediaMetadata.extras?.getString("parserTag")?.let {
            myHlsParserFactory.switchParser(it)
        }
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