package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.Arc
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class ArcDeserializer : JsonDeserializer<Arc?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Arc? {
        val jsonObject = json?.asJsonObject
        val source = jsonObject?.get("S")?.asInt
        val destination = jsonObject?.get("D")?.asInt

        if (source == null || destination == null)
            return null

        return Arc(source, destination)
    }
}