package com.fca.graphviz.api.extensions

import android.util.Log
import com.fca.graphviz.entities.AdjacencyMatrix
import com.fca.graphviz.entities.AdjacencyMatrix.Companion.NO_LINK
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Link
import com.fca.graphviz.entities.Node
import kotlin.system.measureTimeMillis

fun Graph.filter(predicate: (Node) -> Boolean): Graph {
    var newNodes: List<Node> = emptyList()
    val newLinks = ArrayList<Link>()
    val time = measureTimeMillis {
        val indicesSet = HashSet<Int>()
        nodes.forEach { node -> if (predicate.invoke(node)) indicesSet.add(node.id) }

        newNodes = nodes
            .filter { node -> indicesSet.contains(node.id) }
            .map { node -> node.copy() }

        val matrix = AdjacencyMatrix(nodes.map { it.level })
        links.forEach { link -> matrix.addUndirectedLink(link.source, link.target) }
        nodes.map { it.id }
            .filter { !indicesSet.contains(it) }
            .forEach { matrix.removeNode(it) }

        for (i in nodes.indices) {
            for (j in (i + 1) until nodes.size) {
                if (matrix.matrix[i][j] != NO_LINK) newLinks.add(Link(i, j, 1))
            }
        }
    }

    Log.println(Log.INFO, "TimerMessage","Filter time elapsed: $time")

    return Graph(newNodes, newLinks)
}