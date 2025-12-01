package uni.zf.xinpian.player

interface GestureListener {
    fun onHorizontalDistance(distanceX: Float)

    fun onLeftVerticalDistance(distanceY: Float)

    fun onRightVerticalDistance(distanceY: Float)

    fun onGestureEnd()

    fun onSingleTap()

    fun onDoubleTap()
}