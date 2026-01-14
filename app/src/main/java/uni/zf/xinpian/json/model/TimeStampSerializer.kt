package uni.zf.xinpian.json.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

// 自定义序列化器：解析嵌套的time结构，直接提取Long类型的时间戳
object TimeStampSerializer : KSerializer<Long> {
    // 描述符：标记为Long类型
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimeStamp", PrimitiveKind.LONG)

    // 反序列化（JSON → Kotlin）：解析嵌套结构，提取$numberLong的值并转Long
    override fun deserialize(decoder: Decoder): Long {
        // 强制转为JSON解码器，处理嵌套JSON对象
        val jsonDecoder = decoder as JsonDecoder
        val jsonElement = jsonDecoder.decodeJsonElement()
        
        // 逐层解析：time -> $date -> $numberLong
        val numberLongStr = jsonElement.jsonObject
            .getValue("\$date") // 取$date字段（注意转义，直接用字符串匹配）
            .jsonObject
            .getValue("\$numberLong") // 取$numberLong字段
            .jsonPrimitive
            .content // 获取字符串值
        
        // 转换为Long类型（时间戳，单位毫秒）
        return numberLongStr.toLong()
    }

    // 序列化（Kotlin → JSON）：还原嵌套结构（可选，若需要反写JSON则保留）
    override fun serialize(encoder: Encoder, value: Long) {
        val jsonEncoder = encoder as JsonEncoder
        // 构建嵌套的JSON结构
        val jsonObject = buildJsonObject {
            put(
                "\$date", buildJsonObject {
                    put("\$numberLong", value.toString())
                }
            )
        }
        jsonEncoder.encodeJsonElement(jsonObject)
    }
}