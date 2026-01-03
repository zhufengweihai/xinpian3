package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.VideoData
import uni.zf.xinpian.player.BottomItemDecoration
import uni.zf.xinpian.player.EpisodeListAdapter

@OptIn(UnstableApi::class)
fun defaultLoadControl(): DefaultLoadControl {
    return DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            30_000,
            180_000,
            5_000,
            5_000
        )
        .build()
}

fun showDetailsDialog(videoData: VideoData, context: Context) {
    val bottomSheetDialog = BottomSheetDialog(context)
    val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.dialog_details, null)
    bottomSheetDialog.setContentView(bottomSheetView)
    bottomSheetView.findViewById<TextView>(R.id.tv_name).text = videoData.title
    bottomSheetView.findViewById<TextView>(R.id.tv_score).text = videoData.score
    bottomSheetView.findViewById<TextView>(R.id.tv_actor).text = videoData.actorsString()
    bottomSheetView.findViewById<TextView>(R.id.tv_type).text = videoData.typesString()
    bottomSheetView.findViewById<TextView>(R.id.tv_category).text = videoData.categoryString()
    bottomSheetView.findViewById<TextView>(R.id.tv_director).text = videoData.directorsString()
    bottomSheetView.findViewById<TextView>(R.id.tv_area).text = videoData.area
    bottomSheetView.findViewById<TextView>(R.id.tv_year).text = videoData.year
    bottomSheetView.findViewById<TextView>(R.id.tv_description).text = videoData.description
    bottomSheetDialog.show()
}

fun showSourceListDialog(videoData: VideoData, context: Context) {
    val bottomSheetDialog = BottomSheetDialog(context)
    val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.dialog_sources, null)
    bottomSheetDialog.setContentView(bottomSheetView)
    bottomSheetView.findViewById<RecyclerView>(R.id.rv_source_grid).apply {
        this.adapter = adapter
        layoutManager = GridLayoutManager(context, 3)
        addItemDecoration(BottomItemDecoration(context))
    }
    bottomSheetDialog.show()
}

fun showPlayListDialog(videoData: VideoData, context: Context) {
    val bottomSheetDialog = BottomSheetDialog(context)
    val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.dialog_play_list, null)
    bottomSheetDialog.setContentView(bottomSheetView)
    bottomSheetView.findViewById<RecyclerView>(R.id.rv_playlist).apply {
        this.adapter = adapter
        layoutManager = GridLayoutManager(context, 3)
        addItemDecoration(BottomItemDecoration(context))
    }
    bottomSheetDialog.show()
}