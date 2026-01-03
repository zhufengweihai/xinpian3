package uni.zf.xinpian.play

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_UNSPECIFIED
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSpec
import org.apache.commons.net.ftp.FTPClient
import java.io.InputStream

@UnstableApi
class FtpDataSource : BaseDataSource(true) {
        private val ftpClient = FTPClient()
        private var inputStream: InputStream? = null
        private var uri: Uri? = null
        private var opened = false

        override fun open(dataSpec: DataSpec): Long {
            transferInitializing(dataSpec)
            uri = dataSpec.uri.toString().toUri()
            return try {
                // 1. 连接FTP服务器
                ftpClient.connect(uri!!.host, uri!!.port.takeIf { it != -1 } ?: 21)
                // 2. 匿名FTP登录（你的链接不需要账号密码，完美适配）
                ftpClient.login("anonymous", "anonymous")
                // 3. 必须开启被动模式，解决Android防火墙拦截问题
                ftpClient.enterLocalPassiveMode()
                // 4. 二进制流传输，视频文件必加，否则会损坏
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)
                // 5. 获取视频文件输入流
                inputStream = ftpClient.retrieveFileStream(uri!!.path)
                opened = true
                transferStarted(dataSpec)
                // 返回文件大小，未知则返回-1
                if (ftpClient.replyCode == 226) ftpClient.listFiles(uri!!.path)[0].size else -1
            } catch (e: Exception) {
                throw PlaybackException("FTP连接失败", e, ERROR_CODE_IO_UNSPECIFIED)
            }
        }

        override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
            if (length == 0) return 0
            return try {
                val read = inputStream?.read(buffer, offset, length) ?: -1
                if (read == -1) -1 else read
            } catch (e: Exception) {
                throw PlaybackException("FTP读取失败", e, ERROR_CODE_IO_UNSPECIFIED)
            }
        }

        override fun close() {
            if (opened) {
                opened = false
                transferEnded()
                try { inputStream?.close() } catch (_: Exception) {}
                try { if (ftpClient.isConnected) ftpClient.disconnect() } catch (_: Exception) {}
            }
        }

        override fun getUri(): Uri? = uri?.toString()?.toUri()
    }