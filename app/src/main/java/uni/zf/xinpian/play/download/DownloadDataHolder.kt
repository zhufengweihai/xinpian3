package uni.zf.xinpian.play.download

import uni.zf.xinpian.json.model.SourceGroup

/**
 * 内存中持有下载页面所需数据，避免 Intent TransactionTooLarge
 */
object DownloadDataHolder {
    var title: String = ""
    var sourceGroups: List<SourceGroup> = emptyList()

    fun clear() {
        title = ""
        sourceGroups = emptyList()
    }
}
