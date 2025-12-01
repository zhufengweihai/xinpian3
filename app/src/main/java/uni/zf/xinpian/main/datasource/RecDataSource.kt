package uni.zf.xinpian.main.datasource

import android.content.Context
import uni.zf.xinpian.data.model.RecData
import java.io.File

interface RecDataSource {
    fun fetchRecData(context: Context): RecData?

    fun getRecoListFile(context: Context): File {
        var dir = context.getExternalFilesDir(null)
        if (dir == null) {
            dir = context.filesDir
        }
        return File(dir, RECO_LIST_JSON)
    }

    companion object {
        const val RECO_LIST_JSON: String = "rec_list.json"
    }
}

