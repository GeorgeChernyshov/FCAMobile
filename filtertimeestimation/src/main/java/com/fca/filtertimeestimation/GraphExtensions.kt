package com.fca.filtertimeestimation

import android.util.Log
import com.fca.graphviz.api.interactor.FilterInteractor
import com.fca.graphviz.entities.AdjacencyMatrix
import com.fca.graphviz.entities.AdjacencyMatrix.Companion.NO_LINK
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Link
import com.fca.graphviz.entities.Node
import kotlin.system.measureTimeMillis

fun Graph.naiveFilter(
    stabThreshold: Double?,
    deltaThreshold: Int?,
    impactThreshold: Double?,
    pValueThreshold: Double?
): Graph {
    var newNodes: List<Node> = emptyList()
    val newLinks = ArrayList<Link>()

    val predicate: (Node) -> Boolean = { node ->
        (stabThreshold?.let { node.stab >= it } ?: true) &&
                (deltaThreshold?.let { node.delta >= it } ?: true) &&
                (impactThreshold?.let { node.impact >= it } ?: true) &&
                (pValueThreshold?.let { node.pvalue >= it } ?: true)
    }

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

    return Graph(newNodes.toMutableList(), newLinks)
}