package com.fca.graphviz.entities

class AdjacencyTable(nodesIndexes: List<Int>) {
    private val indexesMap = HashMap<Int, Int>()

    val nodes = Array(nodesIndexes.size) { index ->
        indexesMap[nodesIndexes[index]] = index
        AdjacencyNode(index)
    }

    fun addUndirectedLink(sourceIndex: Int, targetIndex: Int) {
        val source = indexesMap[sourceIndex]
        val target = indexesMap[targetIndex]
        if (source == null || target == null) return

        nodes[source].adjacentNodes.add(target)
        nodes[target].adjacentNodes.add(source)
    }

    fun removeUndirectedLink(sourceIndex: Int, targetIndex: Int) {
        val source = indexesMap[sourceIndex]
        val target = indexesMap[targetIndex]
        if (source == null || target == null) return

        nodes[source].adjacentNodes.remove(target)
        nodes[target].adjacentNodes.remove(source)
    }

    inner class AdjacencyNode(val index: Int) {
        val adjacentNodes = mutableListOf<Int>()
    }
}