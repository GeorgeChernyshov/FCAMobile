package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.Extent
import com.fca.fcapstographviz.entities.Intent
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class ExtentDeserializer : JsonDeserializer<Extent?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Extent? {
        try {
            val jsonObject = json?.asJsonObject
            val count = jsonObject?.get("Count")?.asInt ?: 0
            val indices = jsonObject?.getAsJsonArray("Inds")
                ?.map { it.asInt }
                ?: emptyList()

            val names = jsonObject?.getAsJsonArray("Names")
                ?.map { it.asString }
                ?: emptyList()

            return Extent(count, indices, names)
        }
        catch (ex: Exception) {
            return null
        }
    }
}