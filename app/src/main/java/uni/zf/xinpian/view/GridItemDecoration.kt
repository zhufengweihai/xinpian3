package uni.zf.xinpian.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val space: Int, private val spanCount: Int = 3) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        // 均匀分配水平间距，使每列item宽度一致
        outRect.left = space - column * space / spanCount
        outRect.right = (column + 1) * space / spanCount

        // 垂直间距：第一行无顶部间距，其余行顶部留间距
        outRect.top = if (position < spanCount) 0 else space
        outRect.bottom = 0
    }
}