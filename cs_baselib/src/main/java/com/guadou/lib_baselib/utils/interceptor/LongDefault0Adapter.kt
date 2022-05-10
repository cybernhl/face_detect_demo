package com.guadou.lib_baselib.utils.interceptor

import com.google.gson.*
import java.lang.reflect.Type

class LongDefault0Adapter : JsonDeserializer<Long>, JsonSerializer<Long> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        if (json?.asString.equals("")) {
            return 0L
        }
        return try {
            json!!.asLong
        } catch (e: NumberFormatException) {
            0L
        }
    }

    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}

