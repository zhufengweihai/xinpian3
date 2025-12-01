package uni.zf.xinpian.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uni.zf.xinpian.App
import uni.zf.xinpian.data.model.SearchHistory

class SearchHistoryViewModel : ViewModel() {

    private val searchHistoryDao = App.INSTANCE.appDb.searchHistoryDao()

    fun getAllSearchHistory() = searchHistoryDao.getAll()

    fun saveSearchHistory(searchHistory: SearchHistory) = viewModelScope.launch {
        searchHistoryDao.insert(searchHistory)
    }

    fun deleteSearchHistory(keyword: String) = viewModelScope.launch {
        searchHistoryDao.delete(keyword)
    }

    fun deleteAllSearchHistory() = viewModelScope.launch {
        searchHistoryDao.deleteAll()
    }
}