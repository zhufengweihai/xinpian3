package uni.zf.xinpian.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.king.app.dialog.AppDialog.dismissDialog
import com.king.app.dialog.AppDialog.showDialog
import com.king.app.dialog.AppDialogConfig
import com.king.app.updater.AppUpdater
import com.king.app.updater.util.PermissionUtils
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ActivityMainBinding
import uni.zf.xinpian.json.model.GithubRelease
import uni.zf.xinpian.utils.AppVersionUtil.getVersionName
import androidx.core.net.toUri
import uni.zf.xinpian.data.AppConst.VPN_URL

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var isDataLoaded = false
    private var pendingApkUrl: String? = null
    private var isShowingAd = false
    private var adView: View? = null
    private var countDownTimer: CountDownTimer? = null
    private val COOL_DOWN = 60 * 1000
    private var lastShowTime = 0L
    private var isCheckedUpdate = false

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingApkUrl?.let { AppUpdater(this, it).start() }
            } else {
                Toast.makeText(this, "通知权限未授权！", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        val current = System.currentTimeMillis()
        if (current - lastShowTime > COOL_DOWN) {
            lastShowTime = current
            showSplashAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
        countDownTimer?.cancel()
    }

    private fun loadData() {
        viewModel.refreshSecret(this@MainActivity)
        viewModel.refreshImgDomains(this@MainActivity)
    }

    private fun setupUI() {
        binding.viewPager.apply {
            adapter = MainFragmentStateAdapter(this@MainActivity)
            isUserInputEnabled = false
            offscreenPageLimit = 4
        }

        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> binding.viewPager.setCurrentItem(0, false)
                R.id.nav_short -> binding.viewPager.setCurrentItem(1, false)
                R.id.nav_search -> binding.viewPager.setCurrentItem(2, false)
                R.id.nav_discover -> binding.viewPager.setCurrentItem(3, false)
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    private fun checkAppUpdate() {
        if (!isDataLoaded) {
            lifecycleScope.launch {
                viewModel.requestAppUpdateInfo()?.let {
                    val v = it.name.replace("V", "")
                    if (v > getVersionName(this@MainActivity)) {
                        showUpdateDialog(it)
                    }
                }
            }
            isDataLoaded = true
        }
    }

    private fun showUpdateDialog(apkRelease: GithubRelease) {
        val appDialogConfig = AppDialogConfig(this)
            .setTitle("发现新版本: ${apkRelease.name}")
            .setConfirm("升级")
            .setContent(apkRelease.body)
            .setOnClickConfirm {
                appUpdater(apkRelease.assets.first().browserDownloadUrl)
                dismissDialog()
            }
        showDialog(appDialogConfig)
    }

    private fun appUpdater(apkUrl: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionUtils.checkPermission(this, POST_NOTIFICATIONS)) {
                AppUpdater(this, apkUrl).start()
            } else {
                pendingApkUrl = apkUrl
                requestNotificationPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        } else {
            AppUpdater(this, apkUrl).start()
        }
    }

    fun showSplashAd() {
        if (isShowingAd) return
        isShowingAd = true

        adView = LayoutInflater.from(this).inflate(R.layout.layout_splash_ad, null)
        (window.decorView as FrameLayout).addView(
            adView, LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
        adView?.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, VPN_URL.toUri())) }
        val tvSkip = adView?.findViewById<TextView>(R.id.tv_skip)

        countDownTimer = object : CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvSkip?.text = "跳过 ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                hideAd()
            }
        }.start()

        // 手动跳过
        tvSkip?.setOnClickListener { hideAd() }
    }

    private fun hideAd() {
        countDownTimer?.cancel()
        (window.decorView as FrameLayout).removeView(adView)
        isShowingAd = false
        if (!isCheckedUpdate) {
            isCheckedUpdate = true
            checkAppUpdate()
        }
    }
}
