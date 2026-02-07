package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ActivityPlayBinding
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.player.BottomItemDecoration
import uni.zf.xinpian.player.EpisodeChangeListener
import uni.zf.xinpian.player.GestureControl
import uni.zf.xinpian.player.GestureListener
import uni.zf.xinpian.player.parser.MyMediaSourceFactory
import uni.zf.xinpian.utils.TimeUtils.formatMs
import uni.zf.xinpian.utils.toPercent
import uni.zf.xinpian.view.GridItemDecoration
import uni.zf.xinpian.view.SpaceItemDecoration

private val STEPS = intArrayOf(-600000, -60000, -10000, 10000, 60000, 600000)
private const val VOLUME_ADJUSTMENT_FACTOR = 200
private const val MIN_WATCH_TIME = 3000L

@OptIn(UnstableApi::class)
open class PlayActivity : AppCompatActivity(), ControllerVisibilityListener, SourceChangeListener,
    EpisodeChangeListener {
    private val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }
    private lateinit var titleView: TextView
    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var currentSource = 0
    private var currentItem = 0
    private var currentPos: Long = 0
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
                if (isFullScreen) videoData?.let { toggleFullScreen() } else finish()
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
        saveWatchHistory()
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
            //.setMediaSourceFactory(DefaultMediaSourceFactory(MultiDataSourceFactory( this)))
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
        binding.tvIntroduction.setOnClickListener { videoData?.let { showDetailsDialog(it, this) } }
        binding.ivIntroduction.setOnClickListener { videoData?.let { showDetailsDialog(it, this) } }
        initSourceListView()
        initPlayListView()
        initRecommendListView()
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
        binding.ivSources.setOnClickListener { videoData?.let { showSourceListDialog(it) } }
    }

    fun showSourceListDialog(videoData: VideoData) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_sources, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.findViewById<RecyclerView>(R.id.rv_source_grid).apply {
            adapter = SourceListAdapter(this@PlayActivity, videoData.sourceGroups, currentSource, true)
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(BottomItemDecoration(context))
        }
        bottomSheetView.findViewById<View>(R.id.tv_back).setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetView.findViewById<View>(R.id.iv_back).setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetDialog.show()
    }

    private fun initPlayListView() {
        binding.rvItems.adapter = PlayListAdapter(this)
        binding.rvItems.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        binding.rvItems.addItemDecoration(SpaceItemDecoration(this))
        binding.ivItemsMore.setOnClickListener { videoData?.let { showPlayListDialog(it) } }
    }

    fun showPlayListDialog(videoData: VideoData) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_play_list, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.findViewById<RecyclerView>(R.id.rv_playlist).apply {
            this.adapter = PlayListAdapter(
                this@PlayActivity,
                videoData.sourceGroups[currentSource].playList,
                player?.currentMediaItemIndex ?: 0,
                true
            )
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(BottomItemDecoration(context))
        }
        bottomSheetView.findViewById<View>(R.id.tv_back).setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetView.findViewById<View>(R.id.iv_back).setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetDialog.show()
    }

    private fun initRecommendListView() {
        binding.rvRecommend.adapter = RelatedVideoAdapter()
        binding.rvRecommend.layoutManager = GridLayoutManager(this, 3)
        binding.rvRecommend.addItemDecoration(GridItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
    }

    private fun setupClickListener(button: TextView, step: Int) {
        button.setOnClickListener { player?.seekTo(player!!.currentPosition + step) }
    }

    private fun loadData() {
        viewModel.requestVideoData()
        lifecycleScope.launch {
            try {
                viewModel.getVideoData().collect {
                    it?.let {
                        videoData = it
                        initWatchHistory()
                        play(it)
                        updateUI(it)
                        loadRelatedVideos(it)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        lifecycleScope.launch {
            viewModel.getRelatedVideos().collect {
                val adapter = binding.rvRecommend.adapter as RelatedVideoAdapter
                adapter.updateRelatedVideos(it)
            }
        }
    }

    private suspend fun initWatchHistory() {
        viewModel.getWatchHistory()?.let { wh ->
            videoData?.let {
                currentSource = it.sourceGroups.indexOfFirst { it.id == wh.sourceId }.takeIf { it != -1 } ?: 0
                currentItem = wh.currentItem
                currentPos = wh.currentPos
            }
        }
    }

    private fun loadRelatedVideos(video: VideoData) {
        viewModel.requestRelatedVideos(video)
    }

    private fun play(video: VideoData) {
        player?.let {
            val mediaItems = buildMediaItems(video)
            if (mediaItems.isEmpty()) return
            val startIndex = if (currentItem < mediaItems.size) currentItem else 0
            it.setMediaItems(mediaItems, startIndex, currentPos)
            it.prepare()
        }
    }

    private fun buildMediaItems(video: VideoData): List<MediaItem> {
        return video.sourceGroups.elementAtOrNull(currentSource)?.playList?.map {
            val metadata = MediaMetadata.Builder().setTitle(it.sourceName).build()
            MediaItem.Builder().setUri(it.url).setMediaMetadata(metadata).build()
        } ?: listOf()
    }

    private fun updateUI(video: VideoData) {
        binding.nameView.text = video.title
        binding.tvScore.text = "豆瓣评分：%s".format(video.score)
        binding.tvTypes.text = video.typesString()
        val sourceListAdapter = binding.rvSources.adapter as SourceListAdapter
        sourceListAdapter.updateSources(video.sourceGroups, currentSource)
        binding.tvMask.text = video.mask
        val playListAdapter = binding.rvItems.adapter as PlayListAdapter
        if (video.sourceGroups.isNotEmpty()) {
            playListAdapter.updateItems(video.sourceGroups[currentSource].playList)
        }
    }

    override fun onSource(sourceIndex: Int) {
        videoData?.let {
            currentSource = sourceIndex
            currentItem = player?.currentMediaItemIndex ?: 0
            currentPos = player?.currentPosition ?: 0
            play(it)
        }
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

    private fun saveWatchHistory() {
        if (player == null) return
        if (player!!.currentPosition < MIN_WATCH_TIME) return
        videoData?.let {
            viewModel.saveWatchHistory(
                it.toWatchHistory(
                    currentSource,
                    player!!.currentMediaItemIndex,
                    player!!.currentPosition,
                    getPercent()
                )
            )
        }
    }

    private fun getPercent() = toPercent(player?.let { it.currentPosition.toDouble() / it.duration } ?: 0.0)

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
                if (!isLock) if (it.isPlaying) it.pause() else it.play()
            }
        }
    }
}