package uni.zf.xinpian.play

import android.content.Context
import android.os.Bundle
import androidx.core.net.toUri
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaItemStatus
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.MediaSessionStatus
import androidx.mediarouter.media.RemotePlaybackClient

/**
 * DLNA 投屏辅助类
 * 使用 Android MediaRouter API 发现局域网内的 DLNA 设备并投屏播放
 */
class CastHelper(private val context: Context) {

    private val mediaRouter = MediaRouter.getInstance(context)
    private var remotePlaybackClient: RemotePlaybackClient? = null
    private var listener: CastListener? = null
    private var isCasting = false

    val routeSelector: MediaRouteSelector = MediaRouteSelector.Builder()
        .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
        .build()

    private val routerCallback = object : MediaRouter.Callback() {
        override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo, reason: Int) {
            connectToRoute(route)
        }

        override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo, reason: Int) {
            disconnect()
        }
    }

    fun setListener(listener: CastListener) {
        this.listener = listener
    }

    fun startDiscovery() {
        mediaRouter.addCallback(routeSelector, routerCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN)
    }

    fun stopDiscovery() {
        mediaRouter.removeCallback(routerCallback)
    }

    fun getAvailableRoutes(): List<MediaRouter.RouteInfo> {
        return mediaRouter.routes.filter { route ->
            route.matchesSelector(routeSelector) && !route.isDefault
        }
    }

    fun selectRoute(route: MediaRouter.RouteInfo) {
        mediaRouter.selectRoute(route)
    }

    private fun connectToRoute(route: MediaRouter.RouteInfo) {
        remotePlaybackClient?.release()
        remotePlaybackClient = RemotePlaybackClient(context, route)
        isCasting = true
        listener?.onCastConnected(route.name)
    }

    fun cast(videoUrl: String, title: String) {
        val client = remotePlaybackClient ?: return
        if (!client.hasSession()) {
            client.startSession(null, object : RemotePlaybackClient.SessionActionCallback() {
                override fun onResult(data: Bundle, sessionId: String, sessionStatus: MediaSessionStatus?) {
                    playOnRemote(client, videoUrl, title)
                }

                override fun onError(error: String?, code: Int, data: Bundle?) {
                    listener?.onCastError("连接失败: $error")
                }
            })
        } else {
            playOnRemote(client, videoUrl, title)
        }
    }

    private fun playOnRemote(client: RemotePlaybackClient, videoUrl: String, title: String) {
        val mimeType = when {
            videoUrl.contains(".m3u8", ignoreCase = true) -> "application/x-mpegURL"
            videoUrl.contains(".mp4", ignoreCase = true) -> "video/mp4"
            videoUrl.contains(".mkv", ignoreCase = true) -> "video/x-matroska"
            videoUrl.contains(".flv", ignoreCase = true) -> "video/x-flv"
            else -> "video/*"
        }
        val metadata = Bundle().apply {
            putString("title", title)
        }
        client.play(videoUrl.toUri(), mimeType, metadata, 0, null,
            object : RemotePlaybackClient.ItemActionCallback() {
                override fun onResult(data: Bundle, sessionId: String, sessionStatus: MediaSessionStatus?, itemId: String, itemStatus: MediaItemStatus) {
                    listener?.onCastStarted(title)
                }

                override fun onError(error: String?, code: Int, data: Bundle?) {
                    listener?.onCastError("投屏失败: $error")
                }
            })
    }

    fun pause() {
        remotePlaybackClient?.pause(null, null)
    }

    fun resume() {
        remotePlaybackClient?.resume(null, null)
    }

    fun stop() {
        remotePlaybackClient?.stop(null, null)
    }

    fun disconnect() {
        val wasCasting = isCasting
        remotePlaybackClient?.endSession(null, null)
        remotePlaybackClient?.release()
        remotePlaybackClient = null
        isCasting = false
        if (wasCasting) {
            listener?.onCastDisconnected()
        }
    }

    fun isCasting(): Boolean = isCasting

    fun release() {
        stopDiscovery()
        disconnect()
    }

    interface CastListener {
        fun onCastConnected(deviceName: String)
        fun onCastDisconnected()
        fun onCastStarted(title: String)
        fun onCastError(message: String)
    }
}
