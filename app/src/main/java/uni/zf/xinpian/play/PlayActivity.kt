package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.FrameLayout.GONE
import android.widget.FrameLayout.VISIBLE
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.category.VideoListAdapter
import uni.zf.xinpian.data.model.DownloadItem
import uni.zf.xinpian.data.model.Episode
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.data.model.VideoData
import uni.zf.xinpian.data.model.fromVideo
import uni.zf.xinpian.databinding.ActivityPlayBinding
import uni.zf.xinpian.download.DownloadTracker
import uni.zf.xinpian.player.BottomItemDecoration
import uni.zf.xinpian.player.DownloadEpisodesAdapter
import uni.zf.xinpian.player.EpisodeChangeListener
import uni.zf.xinpian.player.EpisodeListAdapter
import uni.zf.xinpian.player.GestureControl
import uni.zf.xinpian.player.GestureListener
import uni.zf.xinpian.player.PlayerViewModel
import uni.zf.xinpian.player.parser.MyMediaSourceFactory
import uni.zf.xinpian.source.startVodSync
import uni.zf.xinpian.utils.TimeUtils.formatMs
import uni.zf.xinpian.utils.getSourceName
import uni.zf.xinpian.utils.toDownloadVideo
import uni.zf.xinpian.utils.toPercent
import uni.zf.xinpian.view.SpaceItemDecoration

@OptIn(UnstableApi::class)
open class PlayActivity : AppCompatActivity(), ControllerVisibilityListener,
    EpisodeChangeListener {
    private val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }
    private lateinit var titleView: TextView
    private var player: ExoPlayer? = null
    private var factory: MyMediaSourceFactory? = null
    private val viewModel: PlayerViewModel by viewModels()
    private var videoData: VideoData? = null
    private var loading = false
    private var isFullScreen = false
    private var systemVisibility = 0
    private var originHeight = 0
    private var isLock = false
    private val hideRunnable = Runnable { binding.lockView.visibility = GONE }
    private val loadingRunnable = Runnable { binding.loadingView.isVisible = loading }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initData(savedInstanceState)
        initView()
        onBackPressedDispatcher()
    }

    private fun onBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullScreen) {
                    videoData?.let { toggleFullScreen()}
                } else {
                    finish()
                }
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initPlayerAndPlay()
            binding.playerView.onResume()
            player?.play()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initPlayerAndPlay()
            binding.playerView.onResume()
            player?.play()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            binding.playerView.onPause()
            player?.pause()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            binding.playerView.onPause()
            player?.pause()
            //releasePlayer()
        }
        saveWatchRecord()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            player?.currentMediaItem?.localConfiguration?.uri?.let {
                viewModel.pauseLastDownload(it)
            }
        }
        releasePlayer()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.isEmpty() -> return
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> initPlayerAndPlay()
            else -> {
                Toast.makeText(applicationContext, "访问存储空间的权限被拒绝", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_VIDEO_ID, videoData?.video?.id)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    override fun onVisibilityChanged(visibility: Int) {
        binding.lockView.visibility = visibility
    }

    private fun initPlayerAndPlay() {
        if (player == null) {
            factory = MyMediaSourceFactory(DownloadTracker.dataSourceFactory)
            player = initPlayer()
            binding.playerView.player = player
            videoData?.let { toPlay() }
        }
    }

    private fun initPlayer(): ExoPlayer {
        return ExoPlayer.Builder(this)
            .setMediaSourceFactory(factory!!)
            .setLoadControl(defaultLoadControl())
            .build().apply {
                addListener(PlayerEventListener())
                setAudioAttributes(AudioAttributes.DEFAULT, true)
                playWhenReady = true
            }
    }

    private fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.playerView.setControllerVisibilityListener(this)
        binding.playerView.requestFocus()
        GestureControl(this, binding.playerView).setOnGestureControlListener(PlayerGestureListener())
        titleView = binding.playerView.findViewById(R.id.title)
        initBackView()
        binding.playerView.findViewById<View>(R.id.fullscreen).setOnClickListener { toggleFullScreen() }
        initLockView()
        initFastStepsView()
        initSourceListView()
        initEpisodeListView()
    }

    private fun initBackView() {
        binding.playerView.findViewById<View>(R.id.back).setOnClickListener {
            if (isFullScreen) videoData?.let { toggleFullScreen(it.isDuanju()) } else finish()
        }
    }

    private fun toggleFullScreen(isPortrait: Boolean = false) {
        isFullScreen = !isFullScreen
        val layoutParams = binding.playerView.layoutParams.apply {
            if (isFullScreen) {
                originHeight = height
                window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                systemVisibility = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            } else {
                window.clearFlags(FLAG_FULLSCREEN)
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = originHeight
                window.decorView.systemUiVisibility = systemVisibility
            }
        }
        if (!isPortrait) {
            requestedOrientation = if (isFullScreen) SCREEN_ORIENTATION_LANDSCAPE else SCREEN_ORIENTATION_PORTRAIT
        }
        binding.playerView.layoutParams = layoutParams
        binding.root.setBackgroundColor(if (isFullScreen) Color.BLACK else Color.WHITE)
    }

    private fun initLockView() {
        binding.lockView.setOnClickListener {
            isLock = !isLock
            val resource = if (isLock) R.drawable.icon_lock else R.drawable.icon_unlock
            binding.lockView.setImageResource(resource)
            hideLockView()
            binding.playerView.performClick()
        }
    }

    private fun hideLockView() {
        binding.playerView.removeCallbacks(hideRunnable)
        binding.playerView.postDelayed(hideRunnable, 5000)
    }

    private fun initFastStepsView() {
        setupClickListener(binding.fastSteps.pre10mButton, STEPS[0])
        setupClickListener(binding.fastSteps.pre1mButton, STEPS[1])
        setupClickListener(binding.fastSteps.pre10sButton, STEPS[2])
        setupClickListener(binding.fastSteps.next10sButton, STEPS[3])
        setupClickListener(binding.fastSteps.next1mButton, STEPS[4])
        setupClickListener(binding.fastSteps.next10mButton, STEPS[5])
    }

    private fun showSourceList() {
        videoData?.let {
            val popupMenu = PopupMenu(this, binding.sourceView)
            it.vodList.forEachIndexed { index, vod ->
                popupMenu.menu.add(0, index, 0, getSourceName(vod.vodId))
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                videoData?.let {
                    it.updateVod(menuItem.itemId)
                    updateUI(it)
                    switchSourceAndPlay()
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun initSourceListView() {
        binding.rvSources.adapter = EpisodeListAdapter(this)
        binding.rvSources.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvSources.addItemDecoration(SpaceItemDecoration(this))
    }

    private fun initEpisodeListView() {
        binding.rvEpisodes.adapter = EpisodeListAdapter(this)
        binding.rvEpisodes.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvEpisodes.addItemDecoration(SpaceItemDecoration(this))
    }

    private fun initRecommendListView() {
        binding.rvRecommend.adapter = VideoListAdapter(this)
        binding.rvRecommend.layoutManager = GridLayoutManager(this, 3)
        binding.rvRecommend.addItemDecoration(SpaceItemDecoration(this))
    }

    private fun showBottomDetailsDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_details, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        videoData?.let {
            bottomSheetView.findViewById<TextView>(R.id.details_name_view).text = it.video.name
            bottomSheetView.findViewById<TextView>(R.id.director_view).text = it.video.directors.joinToString(", ")
            bottomSheetView.findViewById<TextView>(R.id.actor_view).text = it.video.actors.joinToString(", ")
            bottomSheetView.findViewById<TextView>(R.id.full_details_view).text = it.video.plot
        }
        bottomSheetDialog.show()
    }

    private fun showBottomSeriesDialog() {
        videoData?.currentVod()?.let {
            val pos = player?.currentMediaItemIndex ?: 0
            val adapter = EpisodeListAdapter(this, it.episodes, pos)
            showBottomSeriesDialog(adapter)
        }
    }

    private fun showDownloadSeriesDialog() {
        videoData?.let {
            val adapter = DownloadEpisodesAdapter(this, it.currentEpisodes())
            showBottomSeriesDialog(adapter)
            lifecycleScope.launch {
                viewModel.getAllItemsByVideoId(it.video.id).collect(adapter::setDownloadList)
            }
        }
    }

    private fun showBottomSeriesDialog(adapter: EpisodeListAdapter) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_episodes, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.findViewById<RecyclerView>(R.id.bottom_series_view).apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(BottomItemDecoration(context))
        }
        bottomSheetDialog.show()
    }

    private fun setupClickListener(button: TextView, step: Int) {
        button.setOnClickListener { player?.seekTo(player!!.currentPosition + step) }
    }

    private fun initData(state: Bundle?) {
        val videoId = state?.getString(KEY_VIDEO_ID) ?: run { intent.getStringExtra(KEY_VIDEO_ID) }
        videoId?.let { collectVideoData(it) } ?: run {
            Toast.makeText(applicationContext, "错误的视频ID", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun collectVideoData(videoId: String) {
        lifecycleScope.launch {
            viewModel.getVideoDataFlow(videoId).collect { it?.let(::updateAndPlay) }
        }
    }

    private fun updateAndPlay(data: VideoData) {
        videoData = data
        if (data.vodList.isEmpty() || data.video.needUpdate()) {
            startVodSync(this, ArrayList(data.video.vodIds))
        }
        toPlay()
        updateUI(data)
    }

    private fun updateUI(data: VideoData) {
        binding.nameView.text = data.video.name
        if (data.isDuanju()) {
            binding.portraitFullButton.isVisible = true
            binding.ratingBar.isGone = true
            binding.ratingView.isGone = true
        } else {
            binding.portraitFullButton.isGone = true
            binding.ratingBar.isVisible = true
            binding.ratingView.isVisible = true
            binding.ratingBar.rating = ratingStar(data.video.dbScore)
            binding.ratingView.text = score(data.video.dbScore)
        }
        binding.statusView.text = if (data.vodList.isEmpty()) data.video.status else data.currentVod().status
        binding.descView.text = videoDesc(data.video)
        binding.plotView.text = data.video.plot
        if (data.vodList.isNotEmpty()) {
            binding.sourceView.text = getSourceName(data.currentVodId())
            val adapter = binding.episodeListView.adapter as EpisodeListAdapter
            val currentEpisode = player?.currentMediaItemIndex ?: 0
            adapter.updateEpisodes(data.currentEpisodes(), currentEpisode)
        }
    }

    private fun defaultLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                30_000,
                180_000,
                5_000,
                5_000
            )
            .build()
    }

    private fun switchSourceAndPlay() {
        if (player != null && videoData != null && videoData!!.vodList.isNotEmpty()) {
            factory?.switchParser(videoData!!.currentSource())
            val mediaItems = buildMediaItems(videoData!!.currentEpisodes())
            if (mediaItems.isEmpty()) return
            val index = player!!.currentMediaItemIndex
            val startIndex = if (index < mediaItems.size) index else 0
            player!!.setMediaItems(mediaItems, startIndex, player!!.currentPosition)
            player!!.prepare()
        }
    }

    private fun toPlay() {
        if (player == null || videoData == null || videoData!!.vodList.isEmpty()) return
        factory?.switchParser(videoData!!.currentSource())
        val mediaItems = buildMediaItems(videoData!!.currentEpisodes())
        if (mediaItems.isEmpty()) return
        if (player!!.playWhenReady && player!!.playbackState == Player.STATE_READY) {
            player!!.setMediaItems(mediaItems, false)
        } else {
            videoData!!.watchRecord?.let {
                player!!.setMediaItems(mediaItems, it.currentItem, it.currentPos)
            } ?: run {
                player!!.setMediaItems(mediaItems, 0, 0)
            }
            player!!.prepare()
        }
    }

    private fun buildMediaItems(episodes: List<Episode>): List<MediaItem> {
        return episodes.map {
            val metadata = MediaMetadata.Builder().setTitle(it.title).build()
            val uri = (if (it.url.startsWith("http")) it.url else "file://${it.url}").toUri()
            MediaItem.Builder().setUri(uri).setMediaMetadata(metadata).build()
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        binding.playerView.player = null
    }

    private fun saveWatchRecord() {
        if (player == null) return
        if (player!!.currentPosition < MIN_WATCH_TIME) return
        videoData?.let {
            val record = fromVideo(it, player!!.currentMediaItemIndex, player!!.currentPosition, getPercent())
            lifecycleScope.launch {
                viewModel.saveWatchRecord(record)
            }
        }
    }

    private fun getPercent(): String = toPercent(player?.let { it.currentPosition.toDouble() / it.duration } ?: 0.0)

    override fun onEpisode(itemIndex: Int) {
        val prevUri = player?.currentMediaItem?.localConfiguration?.uri
        player?.seekTo(itemIndex, 0)
        if (prevUri != null && prevUri != player?.currentMediaItem?.localConfiguration?.uri) {
            viewModel.pauseLastDownload(prevUri)
        }
    }

    override fun onDownload(itemIndex: Int) {
        lifecycleScope.launch {
            videoData?.let {
                viewModel.saveDownloadVideo(it.toDownloadVideo())
                viewModel.saveDownloadItem(DownloadItem.fromVideo(it.video, it.currentVod(), itemIndex))
                DownloadTracker.startDownload(player!!.getMediaItemAt(itemIndex))
            }
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            loading = playbackState == Player.STATE_BUFFERING
            if (loading) binding.playerView.postDelayed(loadingRunnable, 2000)
            else binding.loadingView.isGone = true
        }

        override fun onPlayerError(error: PlaybackException) {
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                player?.apply {
                    seekToDefaultPosition()
                    prepare()
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let {
                it.mediaMetadata.title?.let { titleView.text = it }
                DownloadTracker.startDownload(it)
                val adapter = binding.episodeListView.adapter as EpisodeListAdapter
                adapter.updateEpisodes(player?.currentMediaItemIndex ?: 0)
            }
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private inner class PlayerGestureListener : GestureListener {
        private var deltaPos: Long = 0
        private var initialVolume: Float = 0f

        override fun onHorizontalDistance(distanceX: Float) {
            if (player == null || isLock) return
            binding.positionView.visibility = VISIBLE
            deltaPos -= (distanceX * VOLUME_ADJUSTMENT_FACTOR).toLong()
            val position = "${formatMs(deltaPos + player!!.currentPosition)} / ${formatMs(player!!.duration)}"
            binding.positionView.text = position
        }

        override fun onLeftVerticalDistance(distanceY: Float) {
            adjustVolume(distanceY)
        }

        private fun adjustVolume(distanceY: Float) {
            val audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager ?: return
            binding.volumeLayout.root.isVisible = true
            if (initialVolume == 0f) {
                initialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
            }
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            initialVolume = (initialVolume + maxVolume * distanceY / binding.playerView.height)
                .coerceIn(0f, maxVolume.toFloat())
            binding.volumeLayout.voiceView.text = "${(initialVolume * 100 / maxVolume).toInt()}%"
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initialVolume.toInt(), 0)
        }

        override fun onRightVerticalDistance(distanceY: Float) {
            adjustBrightness(distanceY)
        }

        private fun adjustBrightness(distanceY: Float) {
            binding.lightnessLayout.root.isVisible = true
            val layoutParams = window.attributes
            var brightness = layoutParams.screenBrightness.takeIf { it >= 0 } ?: 0.5f

            brightness += distanceY / binding.playerView.height
            brightness = brightness.coerceIn(0f, 1f)
            layoutParams.screenBrightness = brightness
            binding.lightnessLayout.lightView.text = "${(brightness * 100).toInt()}%"
            window.attributes = layoutParams
        }

        override fun onGestureEnd() {
            handleGestureEnd()
        }

        private fun handleGestureEnd() {
            if (player == null || isLock) return
            binding.positionView.visibility = GONE
            binding.lightnessLayout.root.visibility = GONE
            binding.volumeLayout.root.visibility = GONE
            if (deltaPos != 0L) {
                player!!.seekTo(deltaPos + player!!.currentPosition)
                deltaPos = 0
            }
            initialVolume = 0f
        }

        override fun onSingleTap() {
            binding.lockView.visibility = VISIBLE
            hideLockView()
            if (!isLock) binding.playerView.performClick()
        }

        override fun onDoubleTap() {
            togglePlayPause()
        }

        private fun togglePlayPause() {
            player?.let {
                if (!isLock) {
                    if (it.isPlaying) it.pause() else it.play()
                }
            }
        }
    }

    companion object {
        const val KEY_VIDEO_ID = "video_id"
        private val STEPS = intArrayOf(-600000, -60000, -10000, 10000, 60000, 600000)
        private const val VOLUME_ADJUSTMENT_FACTOR = 200
        private const val MIN_WATCH_TIME = 10000L
    }
}