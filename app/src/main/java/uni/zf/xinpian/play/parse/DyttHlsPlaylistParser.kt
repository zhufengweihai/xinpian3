package uni.zf.xinpian.play.parse
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist

@UnstableApi
class DyttHlsPlaylistParser : CustomHlsPlaylistParser {
    constructor() : super()
    constructor(
        multivariantPlaylist: HlsMultivariantPlaylist,
        previousMediaPlaylist: HlsMediaPlaylist?
    ) : super(multivariantPlaylist, previousMediaPlaylist)


    override fun filterAdSegments(lines: List<String>): List<String> {
        val toRemove = mutableSetOf<Int>()
        var lastDiscontinuityIndex: Int? = null
        var hasTargetExtinf = false // 标记区间内是否存在目标EXTINF

        for ((index, line) in lines.withIndex()) {
            when {
                // 检测目标EXTINF行（#EXTINF:2.000,）
                line.trim() == "#EXTINF:2.000," -> {
                    hasTargetExtinf = true
                }
                // 检测分隔标记（#EXT-X-DISCONTINUITY）
                line.trim() == TAG_DISCONTINUITY -> {
                    // 已有上一个分隔标记，且区间内存在目标EXTINF → 标记区间内所有行待删除
                    if (lastDiscontinuityIndex != null && hasTargetExtinf) {
                        // 从上个分隔标记的下一行，到当前分隔标记的前一行，全部加入删除列表
                        for (i in lastDiscontinuityIndex + 1 until index) {
                            toRemove.add(i)
                        }
                        // 重置状态，准备下一个区间检测
                        hasTargetExtinf = false
                    }
                    // 更新上一个分隔标记的索引
                    lastDiscontinuityIndex = index
                }
            }
        }

        // 过滤掉待删除的行，返回处理后的结果
        return lines.filterIndexed { index, _ -> index !in toRemove }
    }
}