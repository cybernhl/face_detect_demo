package com.guadou.lib_baselib.utils.interceptor

import com.google.gson.*
import java.lang.reflect.Type

class DoubleDefault0Adapter : JsonDeserializer<Double>, JsonSerializer<Double> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Double {
        if (json?.asString.equals("")) {
            return 0.00
        }
        return try {
            json!!.asDouble
        } catch (e: NumberFormatException) {
            0.00
        }
    }

    override fun serialize(src: Double?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}