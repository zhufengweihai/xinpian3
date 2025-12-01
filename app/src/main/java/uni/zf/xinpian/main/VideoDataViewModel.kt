package uni.zf.xinpian.main

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.RecData
import uni.zf.xinpian.main.datasource.LocalRecDataSource
import uni.zf.xinpian.main.datasource.RemoteRecDataSource

private const val INTERNAL_UPDATE = 1000 * 60 * 60 * 12
private const val INTERNAL_REFRESH = 1000 * 60 * 18
private const val NAME_PREF = "xinpian"
private const val PREF_LAST_SYNC = "last_sync_time"
private const val DEFAULT_LAST_SYNC = 1735660800000

class VideoDataViewModel : ViewModel() {
    private val videoDao by lazy { App.INSTANCE.appDb.videoDao() }
    private val _recData = MutableLiveData<RecData>()
    val recData: LiveData<RecData> get() = _recData

    fun syncVideoData(context: Context, refresh: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val preferences = context.getSharedPreferences(NAME_PREF, MODE_PRIVATE)
                val lastSyncTime = preferences.getLong(PREF_LAST_SYNC, DEFAULT_LAST_SYNC)
                val interval = if (refresh) INTERNAL_REFRESH else INTERNAL_UPDATE
                if (System.currentTimeMillis() - lastSyncTime > interval) {
                    fetchRecDataFromServer(context)
                } else {
                    _recData.postValue(LocalRecDataSource().fetchRecData(context))
                }
            }
        }
    }

    private suspend fun fetchRecDataFromServer(context: Context) {
        val recData = RemoteRecDataSource().fetchRecData(context)
        if (!recData.isEmpty()) {
            videoDao.insertVideosFromServer(recData.videoList())
            val preferences = context.getSharedPreferences(NAME_PREF, MODE_PRIVATE)
            preferences.edit { putLong(PREF_LAST_SYNC, System.currentTimeMillis()) }
        }
        _recData.postValue(recData)
    }
}