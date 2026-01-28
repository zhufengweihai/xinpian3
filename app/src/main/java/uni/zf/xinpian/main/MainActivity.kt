package uni.zf.xinpian.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import io.dcloud.uts.UTSPromise.Companion.resolve
import io.dcloud.uts.compareTo
import uni.UNI69B4A3A.UniUpgradeCenterResult
import uni.UNI69B4A3A.default
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ActivityMainBinding
import uni.zf.xinpian.search.SearchVideoActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var isDataLoaded = false
    private val REQUEST_INSTALL_PERMISSION = 1001
    private var downloadId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        loadData()
    }

    override fun onStart() {
        super.onStart()
        checkAppUpdate()
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun loadData() {
        viewModel.refreshSecret( this@MainActivity)
        viewModel.refreshImgDomains(this@MainActivity)
    }
    private fun setupUI() {
        binding.inputView.setOnClickListener {
            startActivity(Intent(this, SearchVideoActivity::class.java))
        }
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

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    handleDownloadComplete(id)
                }
                unregisterReceiver(this)
            }
        }
    }

    private fun checkAppUpdate() {
        if (isDataLoaded) {
            default().then(fun(uniUpgradeCenterResult) {
                if (uniUpgradeCenterResult.code > 0) {
                    showUpdateDialog(uniUpgradeCenterResult)
                }
                resolve()
            })
            isDataLoaded = false
        }
    }

    private fun showUpdateDialog(result: UniUpgradeCenterResult) {
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle(result.title)
            .setMessage(result.contents)
            .setPositiveButton("更新") { _, _ -> startDownload(result.url.trim()) }
            .setNegativeButton("取消", null)
            .setCancelable(false)
            .show()
    }

    private fun startDownload(url: String) {
        val fileName = "update_${System.currentTimeMillis()}.apk"
        val request = DownloadManager.Request(url.toUri()).apply {
            setTitle("新片")
            setDescription("下载中...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(
                this@MainActivity,
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
        registerDownloadReceiver()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerDownloadReceiver() {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                downloadCompleteReceiver,
                filter,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    RECEIVER_EXPORTED
                } else {
                    RECEIVER_NOT_EXPORTED
                }
            )
        } else {
            registerReceiver(downloadCompleteReceiver, filter)
        }
    }

    @SuppressLint("Range")
    private fun handleDownloadComplete(downloadId: Long) {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

        try {
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    uriString?.let {
                        checkInstallPermission(it.toUri())
                    }
                }
            }
        } finally {
            cursor.close()
        }
    }

    private fun checkInstallPermission(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("安装权限")
                .setMessage("需要允许安装未知来源应用")
                .setPositiveButton("去设置") { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                            data = "package:$packageName".toUri()
                        },
                        REQUEST_INSTALL_PERMISSION
                    )
                }
                .setNegativeButton("取消") { _, _ ->
                    Toast.makeText(this, "无法安装更新", Toast.LENGTH_SHORT).show()
                }
                .show()
        } else {
            installApk(uri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                packageManager.canRequestPackageInstalls()
            ) {
                handleDownloadComplete(downloadId)
            }
        }
    }

    private fun installApk(uri: Uri) {
        try {
            val file = File(uri.path ?: return)
            val contentUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                data = contentUri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            startActivity(installIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "安装失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}