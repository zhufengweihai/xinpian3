package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.FrameLayout.GONE
import android.widget.FrameLayout.VISIBLE
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.category.VideoListAdapter
import uni.zf.xinpian.databinding.ActivityPlayBinding
import uni.zf.xinpian.download.DownloadTracker
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.player.EpisodeChangeListener
import uni.zf.xinpian.player.GestureControl
import uni.zf.xinpian.player.GestureListener
import uni.zf.xinpian.player.parser.MyMediaSourceFactory
import uni.zf.xinpian.utils.TimeUtils.formatMs
import uni.zf.xinpian.view.SpaceItemDecoration

private val STEPS = intArrayOf(-600000, -60000, -10000, 10000, 60000, 600000)
private const val VOLUME_ADJUSTMENT_FACTOR = 200

@OptIn(UnstableApi::class)
open class PlayActivity : AppCompatActivity(), ControllerVisibilityListener, EpisodeChangeListener {
    private val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }
    private lateinit var titleView: TextView
    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var currentSource = 0
    private var factory: MyMediaSourceFactory? = null
    private val viewModel: PlayViewModel by viewModels()
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
        initView()
        onBackPressedDispatcher()
        player = createPlayer()
        binding.playerView.player = player
        loadData()
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
        if (player == null) {
            createPlayer()
            binding.playerView.player = player
        }
        player?.seekTo(playbackPosition)
        player?.play()
    }

    public override fun onStop() {
        super.onStop()
        playbackPosition = player?.currentPosition ?: 0
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    override fun onVisibilityChanged(visibility: Int) {
        binding.lockView.visibility = visibility
    }

    private fun createPlayer(): ExoPlayer {
        factory = MyMediaSourceFactory(DownloadTracker.dataSourceFactory)
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
        initPlayListView()
    }

    private fun initBackView() {
        binding.playerView.findViewById<View>(R.id.back).setOnClickListener {
            if (isFullScreen) videoData?.let { toggleFullScreen() } else finish()
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

    private fun initSourceListView() {
        binding.rvSources.adapter = SourceListAdapter(this)
        binding.rvSources.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvSources.addItemDecoration(SpaceItemDecoration(this))
    }

    private fun initPlayListView() {
        binding.rvItems.adapter = PlayListAdapter(this)
        binding.rvItems.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvItems.addItemDecoration(SpaceItemDecoration(this))
    }

    private fun initRecommendListView() {
        binding.rvRecommend.adapter = VideoListAdapter()
        binding.rvRecommend.layoutManager = GridLayoutManager(this, 3)
        binding.rvRecommend.addItemDecoration(SpaceItemDecoration(this))
    }

    private fun setupClickListener(button: TextView, step: Int) {
        button.setOnClickListener { player?.seekTo(player!!.currentPosition + step) }
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.getVideoData().collect {
                play(it)
            }
        }
    }

    private fun play(video: VideoData) {
        val mediaItems = buildMediaItems(video)
        if (mediaItems.isEmpty()) return
        val index = player!!.currentMediaItemIndex
        val startIndex = if (index < mediaItems.size) index else 0
        player!!.setMediaItems(mediaItems, startIndex, player!!.currentPosition)
        player!!.prepare()
    }

    private fun buildMediaItems(video: VideoData): List<MediaItem> {
        return video.sourceList[currentSource].playList.map {
            val metadata = MediaMetadata.Builder().setTitle(it.sourceName).build()
            MediaItem.Builder().setUri(it.url).setMediaMetadata(metadata).build()
        }
    }

    private fun updateUI(video: VideoData) {
        binding.nameView.text = video.title
        binding.tvScore.text = "豆瓣评分：%s".format(video.score)
        binding.tvTypes.text = video.typesString()
        val sourceListAdapter = binding.rvSources.adapter as SourceListAdapter
        sourceListAdapter.updateSources(video.sourceList, currentSource)
        binding.tvMask.text = video.mask
        val playListAdapter = binding.rvItems.adapter as PlayListAdapter
        playListAdapter.updateItems(video.sourceList[currentSource].playList)
    }

    override fun onEpisode(itemIndex: Int) {
        val prevUri = player?.currentMediaItem?.localConfiguration?.uri
        player?.seekTo(itemIndex, 0)
        if (prevUri != null && prevUri != player?.currentMediaItem?.localConfiguration?.uri) {
            viewModel.pauseLastDownload(prevUri)
        }
    }

    override fun onDownload(itemIndex: Int) {
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
                val adapter = binding.rvItems.adapter as PlayListAdapter
                adapter.updateItems(player?.currentMediaItemIndex ?: 0)
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
}