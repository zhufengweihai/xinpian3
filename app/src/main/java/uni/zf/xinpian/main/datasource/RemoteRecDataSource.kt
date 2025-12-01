package uni.zf.xinpian.main.datasource

import android.content.Context
import android.util.Base64
import com.alibaba.fastjson.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import uni.zf.xinpian.data.model.RecData
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import androidx.core.content.edit
import java.util.concurrent.TimeUnit

class RemoteRecDataSource : RecDataSource {

    override fun fetchRecData(context: Context): RecData {
        return try {
            requestDataFromServer()?.let {
                val data = String(it, Charsets.UTF_8)
                saveToFile(context, data)
                JSON.parseObject(data, RecData::class.java)
            } ?: run { RecData() }
        } catch (e: Exception) {
            e.printStackTrace()
            RecData()
        }
    }

    private fun requestDataFromServer(): ByteArray? {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
            .newCall(Request.Builder().url(URL).build())
            .execute().use { return if (it.isSuccessful) it.body?.bytes() else null }
    }

    @Throws(IOException::class)
    private fun decompressString(base64Bytes: ByteArray): String {
        val compressedBytes = Base64.decode(base64Bytes, Base64.DEFAULT)
        GZIPInputStream(ByteArrayInputStream(compressedBytes)).use { gzipInputStream ->
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                val buffer = ByteArray(1024)
                var len: Int
                while (gzipInputStream.read(buffer).also { len = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, len)
                }
                return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name())
            }
        }
    }

    private fun saveToFile(context: Context, data: String) {
        FileWriter(getRecoListFile(context)).use { it.write(data) }
    }

    private fun updateLastRequestTime(context: Context) {
        val preferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE)
        preferences.edit { putLong(KEY, System.currentTimeMillis()) }
    }

    companion object {
        private const val URL = "https://env-00jx4sldke3h.normal.cloudstatic.cn/xinpian/rec_list.json"
        private const val NAME_PREF = "xinpian"
        private const val KEY = "lastRequestTime"
    }
}