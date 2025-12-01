package uni.zf.xinpian.utils

import uni.zf.xinpian.source.SYMBOL_BFZY
import uni.zf.xinpian.source.SYMBOL_DYZY
import uni.zf.xinpian.source.SYMBOL_FFZY
import uni.zf.xinpian.source.SYMBOL_HDZY
import uni.zf.xinpian.source.SYMBOL_LZZY
import uni.zf.xinpian.source.SYMBOL_WWZY

fun getSourceName(vodId: String): String {
    return when {
        vodId.startsWith(SYMBOL_BFZY) -> "B源"
        vodId.startsWith(SYMBOL_DYZY) -> "D源"
        vodId.startsWith(SYMBOL_FFZY) -> "F源"
        vodId.startsWith(SYMBOL_HDZY) -> "H源"
        vodId.startsWith(SYMBOL_LZZY) -> "L源"
        vodId.startsWith(SYMBOL_WWZY) -> "W源"
        else -> ""
    }
}