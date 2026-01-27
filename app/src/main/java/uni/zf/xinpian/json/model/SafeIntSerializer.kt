package uni.zf.xinpian.json.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SafeIntSerializer : KSerializer<Int> {
    // 描述序列化器类型（Int）
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SafeIntSerializer", PrimitiveKind.INT)

    // 序列化（把Int转成JSON值，这里保持默认）
    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }

    // 反序列化（核心逻辑：处理各种异常值）
    override fun deserialize(decoder: Decoder): Int {
        return try {
            decoder.decodeInt()
        } catch (_: Exception) {
            0
        }
    }
}