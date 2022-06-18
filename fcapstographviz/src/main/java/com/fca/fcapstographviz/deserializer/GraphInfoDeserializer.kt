package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.GraphInfo
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class GraphInfoDeserializer : JsonDeserializer<GraphInfo?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GraphInfo? {
        val jsonObject = json?.asJsonObject

        val nodesCount = jsonObject?.get("NodesCount")?.asInt

        val arcsCount = jsonObject?.get("ArcsCount")?.asInt

        val bottom = jsonObject?.getAsJsonArray("Bottom")
            ?.firstOrNull()
            ?.asInt

        val top = jsonObject?.getAsJsonArray("Top")
            ?.firstOrNull()
            ?.asInt

        if (nodesCount == null ||
            arcsCount == null ||
            bottom == null ||
            top == null
        ) {
            return null
        }

        return GraphInfo(nodesCount, arcsCount, bottom, top)
    }
}