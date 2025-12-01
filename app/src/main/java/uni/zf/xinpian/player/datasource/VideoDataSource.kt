package uni.zf.xinpian.player.datasource

import uni.zf.xinpian.data.model.Video

interface VideoDataSource {
    suspend fun fetchVideo(_id: String): Video?
}
