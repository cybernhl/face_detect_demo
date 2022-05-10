package com.guadou.lib_baselib.utils.interceptor

import com.google.gson.*
import java.lang.reflect.Type

class IntDefaut0Adapter : JsonDeserializer<Int> , JsonSerializer<Int>{

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        if (json?.asString.equals("")) {
            return 0
        }
        return try {
            json!!.asInt
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun serialize(src: Int?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}

