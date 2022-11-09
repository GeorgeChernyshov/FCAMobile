package com.fca.graphviz.entities

/**
 * Adjacency matrix of a graph.
 * In order to use it, it is required that graph indices are sorted ints without skipped indexes
 */
class AdjacencyMatrix(val nodeLevels: List<Int>) {
    var matrix = Array(nodeLevels.size) { Array(nodeLevels.size) { NO_LINK } }

    init {
        for (i in nodeLevels.indices) matrix[i][i] = nodeLevels[i]
    }

    fun addUndirectedLink(sourceIndex: Int, targetIndex: Int) {
        matrix[sourceIndex][targetIndex] = nodeLevels[targetIndex]
        matrix[targetIndex][sourceIndex] = nodeLevels[sourceIndex]
    }

    fun removeUndirectedLink(sourceIndex: Int, targetIndex: Int) {
        matrix[sourceIndex][targetIndex] = NO_LINK
        matrix[targetIndex][sourceIndex] = NO_LINK
    }

    fun removeNode(nodeIndex: Int) {
        val parentLevel = matrix[nodeIndex].maxOf { it }
        val parentIndex = matrix[nodeIndex].indexOf(parentLevel)

        for (i in nodeLevels.indices) {
            if (matrix[nodeIndex][i] != NO_LINK && nodeIndex != i) {
                removeUndirectedLink(nodeIndex, i)
                if (parentIndex != nodeIndex && parentIndex != i) addUndirectedLink(parentIndex, i)
            }
        }
    }

    companion object {
        const val NO_LINK = -1
    }
}