package uni.zf.xinpian.play

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import java.io.IOException

@UnstableApi
class MultiDataSourceFactory(private val context: Context) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        return ProtocolDataSource(context)
    }
}

// 协议选择数据源
@UnstableApi
class ProtocolDataSource(private val context: Context) : BaseDataSource(true) {

    private var dataSource: DataSource? = null

    @OptIn(UnstableApi::class)
    override fun open(dataSpec: DataSpec): Long {
        val uri = dataSpec.uri.toString()

        dataSource = when {
            uri.startsWith("ftp://") -> FtpDataSource()
            else -> DefaultDataSource.Factory(context).createDataSource()
        }

        return dataSource?.open(dataSpec) ?: throw IOException("Cannot open data source")
    }

    override fun getUri() = dataSource?.uri


    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return dataSource?.read(buffer, offset, length) ?: -1
    }

    override fun close() {
        dataSource?.close()
        dataSource = null
    }
}
