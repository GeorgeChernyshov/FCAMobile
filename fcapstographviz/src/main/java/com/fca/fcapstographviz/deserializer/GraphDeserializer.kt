package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.Arc
import com.fca.fcapstographviz.entities.Graph
import com.fca.fcapstographviz.entities.GraphInfo
import com.fca.fcapstographviz.entities.Node
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.Exception
import java.lang.reflect.Type

class GraphDeserializer : JsonDeserializer<Graph?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Graph? {
        val jsonArray = json?.asJsonArray

        val graphInfoDeserializer = GraphInfoDeserializer()
        val nodeDeserializer = NodeDeserializer()
        val arcDeserializer = ArcDeserializer()

        val graphInfoJson = try {
            jsonArray?.get(GRAPH_INFO_POSITION)
        } catch (ex: Exception) {
            null
        }

        val nodesJson = try {
            jsonArray?.get(NODES_POSITION)
                ?.asJsonObject
        } catch (ex: Exception) {
            null
        }

        val arcsJson = try {
            jsonArray?.get(ARCS_POSITION)
                ?.asJsonObject
        } catch (ex: Exception) {
            null
        }

        val graphInfo = graphInfoDeserializer
            .deserialize(
                graphInfoJson,
                GraphInfo::class.java,
                context
            )

        val nodes = nodesJson
            ?.getAsJsonArray("Nodes")
            ?.map { nodeJson ->
                nodeDeserializer.deserialize(
                    nodeJson,
                    Node::class.java,
                    context
                )
            }
            ?.filterNotNull()

        val arcs = arcsJson
            ?.getAsJsonArray("Arcs")
            ?.map { arcJson ->
                arcDeserializer.deserialize(
                    arcJson,
                    Arc::class.java,
                    context
                )
            }
            ?.filterNotNull()

        if (graphInfo == null ||
            nodes == null ||
            arcs == null
        ) {
            return null
        }

        return Graph(graphInfo, nodes, arcs)
    }

    companion object {
        private const val GRAPH_INFO_POSITION = 0
        private const val NODES_POSITION = 1
        private const val ARCS_POSITION = 2
    }
}