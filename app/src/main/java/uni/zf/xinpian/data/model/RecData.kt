package uni.zf.xinpian.data.model

class RecData {
    var top_reco_list: List<Video> = emptyList()
    var latest_tv_list: List<Video> = emptyList()
    var latest_movie_list: List<Video> = emptyList()
    var hot_tv_list: List<Video> = emptyList()
    var hot_variety_list: List<Video> = emptyList()
    var hot_animation_list: List<Video> = emptyList()
    var hot_movie_list: List<Video> = emptyList()
    var hot_duanju_list: List<Video> = emptyList()

    val recLists: Array<List<Video>>
        get() = arrayOf(
            latest_movie_list, latest_tv_list, hot_movie_list, hot_tv_list, hot_variety_list,
            hot_animation_list, hot_duanju_list
        )

    fun videoList(): List<Video> {
        return (latest_movie_list + latest_tv_list + hot_movie_list + hot_tv_list + hot_variety_list +
                hot_animation_list + hot_duanju_list).distinct()
    }

    fun isEmpty() = top_reco_list.isEmpty()
}
