package com.fca.graphviz.api.extensions

import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Node

fun Graph.filter(predicate: (Node) -> Boolean): Graph {
    val indicesSet = HashSet<Int>()
    nodes.forEach { node -> if (predicate.invoke(node)) indicesSet.add(node.id) }

    val newNodes = nodes.filter { node -> indicesSet.contains(node.id) }
    val newLinks = links.filter { link ->
        indicesSet.contains(link.source) && indicesSet.contains(link.target)
    }

    return Graph(newNodes, newLinks)
}