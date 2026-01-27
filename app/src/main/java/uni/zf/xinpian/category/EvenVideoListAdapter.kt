package uni.zf.xinpian.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_VIDEO_ID
import uni.zf.xinpian.json.model.TagData
import uni.zf.xinpian.play.PlayActivity
import uni.zf.xinpian.utils.ImageLoadUtil

class EvenVideoListAdapter(private var tagDataList: List<TagData> = listOf()) : Adapter<EvenVideoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_video, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tagDataList[position])
    }

    override fun getItemCount(): Int = tagDataList.size

    private fun updateLabelView(labelView: TextView, tagData: TagData) {
        val labelText = when (tagData.definition) {
            1 -> "高清"
            3 -> "热门"
            else -> ""
        }
        labelView.visibility = if (labelText.isEmpty()) View.GONE else View.VISIBLE
        labelView.text = labelText
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateVideoList(videoList: List<TagData>) {
        tagDataList = videoList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_image)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val labelView: TextView = itemView.findViewById(R.id.label_view)
        private val statusView: TextView = itemView.findViewById(R.id.status_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)
        init {
            itemView.clipToOutline = true
            itemView.layoutParams.height = itemView.resources.getDimensionPixelSize(R.dimen.v_image_height)
            itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        fun bind(tagData: TagData) {
            ImageLoadUtil.loadImages(imageView,tagData.path)
            scoreView.text = tagData.score
            statusView.text = tagData.mask
            nameView.text = tagData.title
            updateLabelView(labelView, tagData)
            itemView.setOnClickListener { toPlay(it.context, tagData) }
        }
    }

    companion object {
        private fun toPlay(context: Context, tagData: TagData) {
            val intent = Intent(context, PlayActivity::class.java).apply {
                putExtra(ARG_VIDEO_ID, tagData.id)
            }
            context.startActivity(intent)
        }
    }
}