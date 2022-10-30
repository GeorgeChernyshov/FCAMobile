package com.fca.fcapstographviz.api

import android.content.Context
import android.net.Uri
import com.fca.fcapstographviz.deserializer.GsonProvider
import com.fca.fcapstographviz.entities.Graph
import java.io.BufferedReader

object FcapsUtils {
    private val deserializer = GsonProvider.newInstance()

    //TODO split into two functions
    fun getGraph(jsonReader: BufferedReader): com.fca.graphviz.entities.Graph {
        val fcapsGraph = deserializer.fromJson(jsonReader, Graph::class.java)

        return fcapsGraph.toGraphVizGraph()
    }

    private fun Graph.toGraphVizGraph() : com.fca.graphviz.entities.Graph {
        val graphVizNodes = nodes.mapIndexed { index, node ->
            com.fca.graphviz.entities.Node(
                id = index,
                group = 1,
                level = node.extent.count,
                extent = node.extent.names.joinToString(", "),
                intent = node.intent?.names?.joinToString(", ")
            )
        }

        val graphVizLinks = arcs.map { arc ->
            com.fca.graphviz.entities.Link(arc.source, arc.destination, 1)
        }

        return com.fca.graphviz.entities.Graph(graphVizNodes, graphVizLinks)
    }
}