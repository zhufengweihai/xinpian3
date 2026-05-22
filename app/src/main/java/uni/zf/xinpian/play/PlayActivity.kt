package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.GONE
import android.widget.FrameLayout.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
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
import uni.zf.xinpian.data.AppConst.GYLM_URL
import uni.zf.xinpian.data.AppConst.VPN_URL
import uni.zf.xinpian.databinding.ActivityPlayBinding
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.play.download.DownloadDataHolder
import uni.zf.xinpian.play.download.DownloadSelectActivity
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
    private val viewModel: PlayViewModel by viewModels()
    private var videoData: VideoData? = null
    private var loading = false
    private var isFullScreen = false
    private var originHeight = 0
    private var isLock = false
    private val hideRunnable = Runnable { binding.lockView.visibility = GONE }
    private val loadingRunnable = Runnable { binding.loadingView.isVisible = loading }
    private lateinit var castHelper: CastHelper

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        castHelper = CastHelper(this)
        castHelper.setListener(CastEventListener())
        initView()
        onBackPressedDispatcher()
        player = PlayerFactory.createPlayer(this, true)
        player?.addListener(PlayerEventListener())
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
            player = PlayerFactory.createPlayer(this, true)
            player?.addListener(PlayerEventListener())
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
        castHelper.release()
        player?.release()
        player = null
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    override fun onVisibilityChanged(visibility: Int) {
        binding.lockView.visibility = visibility
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
        binding.playerView.findViewById<View>(R.id.cast).setOnClickListener { showCastDeviceDialog() }
        initLockView()
        initFastStepsView()
        binding.tvIntroduction.setOnClickListener { videoData?.let { showDetailsDialog(it, this) } }
        binding.ivIntroduction.setOnClickListener { videoData?.let { showDetailsDialog(it, this) } }
        binding.btnDownload.setOnClickListener { openDownloadSelect() }
        initSourceListView()
        initPlayListView()
        initAdImageView()
        initRecommendListView()
        initOverlayAd()
    }

    private fun initBackView() {
        binding.playerView.findViewById<View>(R.id.back).setOnClickListener {
            if (isFullScreen) videoData?.let { toggleFullScreen() } else finish()
        }
    }

    private fun toggleFullScreen(isPortrait: Boolean = false) {
        isFullScreen = !isFullScreen
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        if (isFullScreen) {
            // 进入全屏横屏
            if (!isPortrait) {
                requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            // 隐藏系统栏
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // PlayerView 填满整个屏幕
            binding.playerLayout.layoutParams = binding.playerLayout.layoutParams.apply {
                originHeight = height
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            binding.playerLayout.requestLayout()

            // 隐藏下方内容区域
            val scrollView = binding.root.getChildAt(1)
            scrollView?.visibility = GONE

            // 移除 padding 让播放器真正全屏
            binding.root.setPadding(0, 0, 0, 0)
            binding.root.setBackgroundColor(Color.BLACK)
        } else {
            // 退出全屏
            if (!isPortrait) {
                requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
            }
            // 显示系统栏
            insetsController.show(WindowInsetsCompat.Type.systemBars())

            // 恢复 PlayerView 高度
            binding.playerLayout.layoutParams = binding.playerLayout.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = originHeight
            }
            binding.playerLayout.requestLayout()

            // 显示下方内容
            val scrollView = binding.root.getChildAt(1)
            scrollView?.visibility = VISIBLE

            // 恢复 insets padding
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            binding.root.requestApplyInsets()
            binding.root.setBackgroundColor(Color.WHITE)
        }
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
        bottomSheetDialog.show()
    }

    private fun initAdImageView() {
        binding.ivAd.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, GYLM_URL.toUri()))
        }
    }

    private fun initRecommendListView() {
        binding.rvRecommend.adapter = RelatedVideoAdapter()
        binding.rvRecommend.layoutManager = GridLayoutManager(this, 3)
        binding.rvRecommend.addItemDecoration(GridItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_space)))
    }

    private fun initOverlayAd() {
        binding.overlayAdClose.setOnClickListener { hideOverlayAd() }
        binding.overlayAdImage.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, VPN_URL.toUri())) }
    }

    private fun showOverlayAd() {
        binding.overlayAdContainer.visibility = VISIBLE
    }

    private fun hideOverlayAd() {
        binding.overlayAdContainer.visibility = GONE
    }

    private fun showCastDeviceDialog() {
        if (castHelper.isCasting()) {
            castHelper.disconnect()
            player?.play()
            Toast.makeText(this, "已断开投屏", Toast.LENGTH_SHORT).show()
            return
        }

        // 点击时开始搜索设备
        Toast.makeText(this, "正在搜索投屏设备...", Toast.LENGTH_SHORT).show()
        castHelper.startDiscovery()

        // 延迟2秒等待设备发现，然后展示结果
        binding.playerView.postDelayed({
            val routes = castHelper.getAvailableRoutes()
            when {
                routes.isEmpty() -> {
                    Toast.makeText(this, "未发现可投屏设备，请确保手机和电视在同一局域网", Toast.LENGTH_LONG).show()
                    castHelper.stopDiscovery()
                }
                routes.size == 1 -> {
                    castHelper.selectRoute(routes[0])
                    castToDevice()
                }
                else -> {
                    val deviceNames = routes.map { it.name }.toTypedArray()
                    AlertDialog.Builder(this)
                        .setTitle("选择投屏设备")
                        .setItems(deviceNames) { _, which ->
                            castHelper.selectRoute(routes[which])
                            castToDevice()
                        }
                        .setNegativeButton("取消") { _, _ -> castHelper.stopDiscovery() }
                        .setOnCancelListener { castHelper.stopDiscovery() }
                        .show()
                }
            }
        }, 2000)
    }

    private fun castToDevice() {
        val currentMediaItem = player?.currentMediaItem ?: return
        val url = currentMediaItem.localConfiguration?.uri?.toString() ?: return
        val title = currentMediaItem.mediaMetadata.title?.toString() ?: videoData?.title ?: "视频"
        player?.pause()
        castHelper.cast(url, title)
    }

    private fun openDownloadSelect() {
        val data = videoData ?: run {
            Toast.makeText(this, "视频数据未加载", Toast.LENGTH_SHORT).show()
            return
        }
        if (data.sourceGroups.isEmpty()) {
            Toast.makeText(this, "无可用片源", Toast.LENGTH_SHORT).show()
            return
        }
        DownloadDataHolder.title = data.title
        DownloadDataHolder.sourceGroups = data.sourceGroups
        startActivity(Intent(this, DownloadSelectActivity::class.java))
    }

    private inner class CastEventListener : CastHelper.CastListener {
        override fun onCastConnected(deviceName: String) {
            runOnUiThread {
                Toast.makeText(this@PlayActivity, "已连接: $deviceName", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCastDisconnected() {
            runOnUiThread {
                Toast.makeText(this@PlayActivity, "已断开投屏", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCastStarted(title: String) {
            runOnUiThread {
                Toast.makeText(this@PlayActivity, "正在投屏: $title", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCastError(message: String) {
            runOnUiThread {
                Toast.makeText(this@PlayActivity, message, Toast.LENGTH_SHORT).show()
                player?.play()
            }
        }
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
            playListAdapter.updateItems(video.sourceGroups[currentSource].playList, currentItem)
        }
        scrollToSource(currentSource)
        scrollToEpisode(currentItem)
    }

    private fun scrollToSource(position: Int) {
        binding.rvSources.post {
            val layoutManager = binding.rvSources.layoutManager as? LinearLayoutManager ?: return@post
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun scrollToEpisode(position: Int) {
        binding.rvItems.post {
            val layoutManager = binding.rvItems.layoutManager as? LinearLayoutManager ?: return@post
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun onSource(sourceIndex: Int) {
        videoData?.let {
            currentSource = sourceIndex
            currentItem = player?.currentMediaItemIndex ?: 0
            currentPos = player?.currentPosition ?: 0
            play(it)
            scrollToSource(currentSource)
        }
    }

    override fun onEpisode(itemIndex: Int) {
        val prevUri = player?.currentMediaItem?.localConfiguration?.uri
        player?.seekTo(itemIndex, 0)
        scrollToEpisode(itemIndex)
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
            if (loading) {
                binding.playerView.postDelayed(loadingRunnable, 2000)
                showOverlayAd()
            } else {
                binding.loadingView.isGone = true
            }

            // 播放中时隐藏广告
            if (playbackState == Player.STATE_READY && player?.isPlaying == true) {
                hideOverlayAd()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                hideOverlayAd()
            } else if (player?.playbackState == Player.STATE_READY) {
                // 暂停时显示广告
                showOverlayAd()
            }
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
                val currentIndex = player?.currentMediaItemIndex ?: 0
                val adapter = binding.rvItems.adapter as PlayListAdapter
                adapter.updateItems(currentIndex)
                scrollToEpisode(currentIndex)
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