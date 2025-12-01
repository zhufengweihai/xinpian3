package uni.zf.xinpian.download

import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uni.zf.xinpian.App

class DownloadObserver(private val delay: Long = 5000) : LifecycleObserver {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val downloadDao = App.INSTANCE.appDb.downloadDao()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        runnable = object : Runnable {
            override fun run() {
                checkDownloadProgress()
                handler.postDelayed(this, delay)
            }
        }
        handler.postDelayed(runnable, delay)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        handler.removeCallbacks(runnable)
    }

    @OptIn(UnstableApi::class)
    private fun checkDownloadProgress() {
        CoroutineScope(Dispatchers.IO).launch {
            DownloadTracker.getCurrentDownloads().forEach {
                downloadDao.updateItem(
                    it.request.uri.toString(),
                    it.state,
                    it.bytesDownloaded,
                    it.percentDownloaded
                )
            }
        }
    }
}
