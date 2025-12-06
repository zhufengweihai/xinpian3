package uni.zf.xinpian.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import uni.zf.xinpian.databinding.ViewTagDataBinding

class TagDataView (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = ViewTagDataBinding.inflate(LayoutInflater.from(context), this, false)
}