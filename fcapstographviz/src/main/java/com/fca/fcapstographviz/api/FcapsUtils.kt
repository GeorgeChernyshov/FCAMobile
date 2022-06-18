package com.fca.fcapstographviz.api

import android.content.Context
import android.net.Uri
import com.fca.fcapstographviz.deserializer.GsonProvider
import com.fca.fcapstographviz.entities.Graph

object FcapsUtils {
    private val deserializer = GsonProvider.newInstance()

    fun getGraph(context: Context, uri: Uri?): com.fca.graphviz.entities.Graph? {
        if (uri == null) return null

        val jsonReader = context.contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?: return null

        val fcapsGraph = deserializer.fromJson(jsonReader, Graph::class.java)

        return fcapsGraph.toGraphVizGraph()
    }

    private fun Graph.toGraphVizGraph() : com.fca.graphviz.entities.Graph? {
        val graphVizNodes = nodes.mapIndexed { index, node ->
            com.fca.graphviz.entities.Node(index.toString(), 1)
        }

        val graphVizLinks = arcs.map { arc ->
            com.fca.graphviz.entities.Link(arc.source.toString(), arc.destination.toString(), 1)
        }

        return com.fca.graphviz.entities.Graph(graphVizNodes, graphVizLinks)
    }
}