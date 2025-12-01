package uni.zf.xinpian.search

interface SearchHistoryListener {
    fun onSearchHistory(history: String, delete: Boolean)
}
