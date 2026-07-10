package com.muedsa.tvbox.anime1.model

import com.github.houbb.opencc4j.util.ZhTwConverterUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = AnimeItemSerializer::class)
data class AnimeItem(
    val cat: Int = 0,           // 索引0: 分类
    val title: String = "",     // 索引1: 标题
    val status: String = "",    // 索引2: 连载状态
    val year: String = "",      // 索引3: 年份
    val season: String = "",    // 索引4: 季节
    val extra: String = "",     // 索引5: 字幕组
)

object AnimeItemSerializer : KSerializer<AnimeItem> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AnimeItem")

    // 序列化：类 → JSON 数组
    override fun serialize(encoder: Encoder, value: AnimeItem) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("该序列化器仅支持 JSON 格式")

        val jsonArray = JsonArray(
            listOf(
                JsonPrimitive(value.cat),
                JsonPrimitive(value.title),
                JsonPrimitive(value.status),
                JsonPrimitive(value.year),
                JsonPrimitive(value.season),
                JsonPrimitive(value.extra)
            )
        )
        jsonEncoder.encodeJsonElement(jsonArray)
    }

    // 反序列化：JSON 数组 → 类
    override fun deserialize(decoder: Decoder): AnimeItem {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("该序列化器仅支持 JSON 格式")

        val jsonArray = jsonDecoder.decodeJsonElement().jsonArray
        require(jsonArray.size == 6) { "数组长度必须为6，实际为 ${jsonArray.size}" }

        return AnimeItem(
            cat = jsonArray[0].jsonPrimitive.int,
            title = ZhTwConverterUtil.toSimple(jsonArray[1].jsonPrimitive.content),
            status = ZhTwConverterUtil.toSimple(jsonArray[2].jsonPrimitive.content),
            year = ZhTwConverterUtil.toSimple(jsonArray[3].jsonPrimitive.content),
            season = ZhTwConverterUtil.toSimple(jsonArray[4].jsonPrimitive.content),
            extra = ZhTwConverterUtil.toSimple(jsonArray[5].jsonPrimitive.content),
        )
    }
}