package uni.zf.xinpian.player

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import uni.zf.xinpian.utils.isInLeft
import uni.zf.xinpian.utils.isInRight
import kotlin.math.abs

/**
 * 播放控手势控制。通过对view的GestureDetector事件做监听，判断水平滑动还是垂直滑动。
 */
class GestureControl(
    private val mContext: Context,
    private val mGesturebleView: View
) {

    private var isGestureEnable = true
    private var isInHorizenalGesture = false
    private var isInRightGesture = false
    private var isInLeftGesture = false
    private val mGestureDetector: GestureDetector
    private var mGestureListener: GestureListener? = null

    init {
        mGestureDetector = GestureDetector(mContext, GestureListenerImpl())
        initGestureDetector()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGestureDetector() {
        mGesturebleView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> resetGestureFlags()
            }
            mGestureDetector.onTouchEvent(event)
        }

        mGestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                mGestureListener?.onSingleTap()
                return false
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                mGestureListener?.onDoubleTap()
                return false
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean = false
        })
    }

    private fun resetGestureFlags() {
        mGestureListener?.onGestureEnd()
        isInLeftGesture = false
        isInRightGesture = false
        isInHorizenalGesture = false
    }

    fun enableGesture(enable: Boolean) {
        isGestureEnable = enable
    }

    fun setOnGestureControlListener(listener: GestureListener?) {
        mGestureListener = listener
    }

    private inner class GestureListenerImpl : GestureDetector.OnGestureListener {
        private var mXDown = 0f

        override fun onSingleTapUp(e: MotionEvent): Boolean = false

        override fun onShowPress(e: MotionEvent) {}

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!isGestureEnable || e1 == null) return false

            if (abs(distanceX) > abs(distanceY)) {
                if (!isInLeftGesture && !isInRightGesture) {
                    isInHorizenalGesture = true
                }
            }

            if (isInHorizenalGesture) {
                handleGesture(distanceX) { mGestureListener?.onHorizontalDistance(it) }
            } else {
                when {
                    isInLeft(mContext, mXDown.toInt()) -> {
                        isInLeftGesture = true
                        handleGesture(distanceY) { mGestureListener?.onLeftVerticalDistance(it) }
                    }
                    isInRight(mContext, mXDown.toInt()) -> {
                        isInRightGesture = true
                        handleGesture(distanceY) { mGestureListener?.onRightVerticalDistance(it) }
                    }
                }
            }
            return true
        }

        private fun handleGesture(distance: Float, consumer: (Float) -> Unit) {
            consumer(distance)
        }

        override fun onLongPress(e: MotionEvent) {}

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean = false

        override fun onDown(e: MotionEvent): Boolean {
            mXDown = e.x
            return true
        }
    }
}