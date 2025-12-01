package uni.zf.xinpian.player.datasource

import io.dcloud.unicloud.UniCloudDBGetResult
import io.dcloud.unicloud.uniCloud
import io.dcloud.uts.await
import uni.zf.xinpian.data.model.Video

class RemoteVideoDataSource : VideoDataSource {
    override suspend fun fetchVideo(_id: String): Video? {
        val db = uniCloud.databaseForJQL()
        val result: UniCloudDBGetResult = await(
            db.collection("video").doc(_id).get()
        )
        return if (result.data.isNotEmpty()) Video.from(result.data[0]) else null
    }
}
