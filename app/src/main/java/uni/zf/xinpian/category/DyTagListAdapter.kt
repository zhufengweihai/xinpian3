package uni.zf.xinpian.category

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import uni.zf.xinpian.json.model.DyTag
import uni.zf.xinpian.view.TagDataView

class DyTagListAdapter(private var list: List<DyTag> = listOf()) : Adapter<DyTagListAdapter.ViewHolder>() {

    fun updateDyTagList(newList: List<DyTag>) {
        val diffCallback = DyTagDiffCallback(this.list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list = newList
        diffResult.dispatchUpdatesTo(this)
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
    
    class DyTagDiffCallback(
        private val oldList: List<DyTag>,
        private val newList: List<DyTag>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        
        override fun getNewListSize() = newList.size
        
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}