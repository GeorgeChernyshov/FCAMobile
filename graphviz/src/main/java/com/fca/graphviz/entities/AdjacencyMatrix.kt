package com.fca.graphviz.entities

import java.util.*
import kotlin.collections.ArrayList

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
        val parentIndexes = ArrayList<Int>()
        val childIndexes = ArrayList<Int>()

        matrix[nodeIndex].forEachIndexed { index, i ->
            if (i != NO_LINK && i != matrix[nodeIndex][nodeIndex]) {
                if (i > matrix[nodeIndex][nodeIndex])
                    parentIndexes.add(index)
                else childIndexes.add(index)
            }
        }

        for (i in nodeLevels.indices) {
            if (matrix[nodeIndex][i] != NO_LINK && nodeIndex != i) {
                removeUndirectedLink(nodeIndex, i)
            }
        }

        for (parentIndex in parentIndexes)
            for (childIndex in childIndexes)
                if (!checkPath(parentIndex, childIndex))
                    addUndirectedLink(parentIndex, childIndex)
    }

    private fun checkPath(parentIndex: Int, childIndex: Int) : Boolean {
        val queue = PriorityQueue<Int>()
        val visitedIndices = HashSet<Int>()
        queue.add(parentIndex)
        visitedIndices.add(parentIndex)

        while (!queue.isEmpty()) {
            val parent = queue.poll() ?: return false
            for (i in nodeLevels.indices) {
                if (matrix[parent][i] != NO_LINK) {
                    if (i == childIndex)
                        return true

                    if (matrix[parent][i] < matrix[parent][parent] &&
                        matrix[parent][i] > matrix[childIndex][childIndex] &&
                        !visitedIndices.contains(i)
                    ) {
                        queue.add(i)
                        visitedIndices.add(i)
                    }
                }
            }
        }

        return false
    }

    companion object {
        const val NO_LINK = -1
    }
}