package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.Intent
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class IntentDeserializer : JsonDeserializer<Intent?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Intent? {
        try {
            val jsonObject = json?.asJsonObject
            val count = jsonObject?.get("Count")?.asInt
            val names = jsonObject?.getAsJsonArray("Names")
                ?.map { it.asString }

            if (count == null || names == null)
                return null

            return Intent(count, names)
        }
        catch (ex: Exception) {
            return null
        }
    }
}