package uni.zf.xinpian.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.WatchHistory

class WatchHistoryViewModel : ViewModel() {
    private val dao = App.INSTANCE.appDb.watchHistoryDao()
    val watchList: Flow<List<WatchHistory>> = dao.getAll()

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun deleteHistory(watchHistory: WatchHistory) {
        return dao.deleteWatchHistory(watchHistory)
    }
}
