package uni.zf.xinpian.record

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.App
import uni.zf.xinpian.data.dao.WatchRecordDao
import uni.zf.xinpian.data.model.WatchRecord

class WatchRecordViewModel : ViewModel() {
    private val dao: WatchRecordDao = App.INSTANCE.appDb.watchRecordDao()
    val watchList: Flow<List<WatchRecord>> = dao.getAll()

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun deleteWatchRecord(watchRecord: WatchRecord) {
        return dao.deleteWatchRecord(watchRecord)
    }
}
