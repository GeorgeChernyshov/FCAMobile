package com.fca.graphviz.entities

class AdjacencyTable(nodesIndexes: List<Int>) {

    val nodes = Array(nodesIndexes.size) { index ->
        AdjacencyNode(index)
    }

    fun addUndirectedLink(source: Int, target: Int) {
        nodes[source].adjacentNodes.add(target)
        nodes[target].adjacentNodes.add(source)
    }

    fun removeUndirectedLink(source: Int, target: Int) {
        nodes[source].adjacentNodes.remove(target)
        nodes[target].adjacentNodes.remove(source)
    }

    fun addDirectedLink(source: Int, target: Int) {
        nodes[source].adjacentNodes.add(target)
    }

    fun removeDirectedLink(source: Int, target: Int) {
        nodes[source].adjacentNodes.remove(target)
    }

    fun reversed() : AdjacencyTable {
        val result = AdjacencyTable(nodes.map { it.index })
        for (nodeIndex in nodes.indices)
            for (adjIndex in nodes[nodeIndex].adjacentNodes)
                result.addDirectedLink(adjIndex, nodeIndex)

        return result
    }

    inner class AdjacencyNode(val index: Int) {
        val adjacentNodes = mutableListOf<Int>()
    }
}