package uni.zf.xinpian.play

import androidx.media3.exoplayer.DefaultLoadControl

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