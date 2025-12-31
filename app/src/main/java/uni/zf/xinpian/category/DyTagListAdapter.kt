package uni.zf.xinpian.category

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.view.TagDataView

class DyTagListAdapter(private var list: List<DyTag> = listOf()) : Adapter<DyTagListAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateDyTagList(list: List<DyTag>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TagDataView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagDataView = itemView as TagDataView

        fun bind(dyTag: DyTag) {
            tagDataView.setTagData(dyTag)
        }
    }
}