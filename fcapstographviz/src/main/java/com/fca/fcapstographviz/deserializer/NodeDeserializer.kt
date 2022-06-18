package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.Extent
import com.fca.fcapstographviz.entities.Intent
import com.fca.fcapstographviz.entities.Node
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.Exception
import java.lang.reflect.Type

internal class NodeDeserializer : JsonDeserializer<Node?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Node? {
        val jsonObject = json?.asJsonObject
        val extent = ExtentDeserializer().deserialize(
            jsonObject?.getAsJsonObject("Ext"),
            Extent::class.java,
            context
        )

        val lStab = jsonObject?.get("LStab")?.asDouble
        val rStab = jsonObject?.get("UStab")?.asInt

        val stab = try {
            jsonObject?.get("Stab")?.asDouble ?: Node.STAB_INF
        } catch (ex: Exception) {
            Node.STAB_INF
        }

        val intent = try {
            IntentDeserializer().deserialize(
                jsonObject?.getAsJsonObject("Int"),
                Intent::class.java,
                context
            )
        } catch (ex: Exception) {
            null
        }

        if (extent == null) return null

        return Node(extent, lStab, rStab, stab, intent)
    }
}