package uni.zf.xinpian.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        outRect.apply {
            // 只在元素之间设置间距，最左边和最右边不设置边距
            left = 0
            right = if (position == itemCount - 1) 0 else space
            top = 0
            bottom = 0
        }
    }
}