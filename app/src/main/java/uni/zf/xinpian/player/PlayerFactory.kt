package uni.zf.xinpian.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultAllocator
import okhttp3.OkHttpClient
import java.io.File

@OptIn(UnstableApi::class) // Media3 的很多高级配置仍标记为 Unstable，这是正常的
object PlayerFactory {

    private var simpleCache: SimpleCache? = null

    // 1. 初始化缓存 (建议单例)
    @Synchronized
    private fun getCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheSize = 10 * 1024 * 1024 * 1024L // 100MB 缓存
            val cacheDir = File(context.cacheDir, "media3_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(cacheSize)
            val databaseProvider = StandaloneDatabaseProvider(context)
            simpleCache = SimpleCache(cacheDir, evictor, databaseProvider)
        }
        return simpleCache!!
    }

    fun createPlayer(context: Context, autoPlay: Boolean = true): ExoPlayer {
        // 2. 配置 LoadControl (缓冲策略)
        // 目标：增加最大缓冲以抵抗网络波动，减少重缓冲；保持较小的起播缓冲以秒开。
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(
                3000,  // minBufferMs: 最小缓冲数据 (默认是 50000) -> 设小一点为了快速seek后播放，但别太小
                3600000, // maxBufferMs: 最大缓冲数据 (默认是 50000) -> 保持默认或更大，适合长视频
                1500,  // bufferForPlaybackMs: 起播需要的缓冲时长 -> 越小起播越快，但太小容易卡顿。1.5s是平衡点
                3000   // bufferForPlaybackAfterRebufferMs: 卡顿后恢复播放需要的时长
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        // 3. 配置 TrackSelector (自适应码率)
        // 目标：在移动网络下限制最大码率，WiFi下自动切高画质
        val trackSelector = DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setMaxVideoSizeSd() // 示例：起播时限制为SD，随后自适应。或者 .setMaxVideoBitrate(2_000_000)
                .setForceHighestSupportedBitrate(false) // 让算法决定，不要强制最高
        )

        // 4. 配置 DataSource (网络与缓存)
        // 目标：使用 OkHttp 处理 HTTP/2 和连接池，外包一层 CacheDataSource
        val okHttpClient = OkHttpClient.Builder().build() // 建议复用全局 OkHttpClient
        val httpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR) // 缓存读取失败时尝试网络

        // 5. 配置 RenderersFactory (解码器)
        // 目标：优先使用硬件解码，但在不支持时允许扩展解码器(如ffmpeg)
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF) // 如果集成了ffmpeg extension，设为 ON

        // 6. 构建 ExoPlayer
        return ExoPlayer.Builder(context, renderersFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)) // 绑定带缓存的 Source
            .setHandleAudioBecomingNoisy(true) // 耳机拔出自动暂停
            .build().apply { playWhenReady = autoPlay }
    }

    fun releaseCache() {
        simpleCache?.release()
        simpleCache = null
    }
}