package uni.zf.xinpian

import android.os.Process
import android.util.Log
import androidx.room.Room.databaseBuilder
import io.dcloud.uniapp.UniApplication
import uni.zf.xinpian.data.AppDatabase

class App : UniApplication() {
    lateinit var appDb: AppDatabase
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appDb = databaseBuilder(applicationContext, AppDatabase::class.java, "xinpian").build()

        // 设置全局未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler { thread: Thread?, throwable: Throwable? ->
            // 强制打印完整异常栈到Logcat
            Log.e("GlobalCrash", "Uncaught exception in thread: " + thread!!.getName(), throwable)
            // 可选：将日志写入本地文件（防止Logcat丢失）
            // saveCrashLogToFile(throwable);
            // 退出应用（模拟系统默认行为）
            Process.killProcess(Process.myPid())
            System.exit(1)
        }
    }

    companion object {
        lateinit var INSTANCE: App
    }
}