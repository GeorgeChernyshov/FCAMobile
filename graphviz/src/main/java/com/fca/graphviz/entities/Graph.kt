package com.fca.graphviz.entities

import java.util.*

data class Graph(
    val nodes: List<Node>,
    val links: List<Link>
) {

    private var adjacencyTable: AdjacencyTable

    init {
        resetIndices()
        adjacencyTable = createAdjacencyTable()
        setLevels()
        findObjectAdditions()
        findAttributeAdditions()
    }

    fun init() {
        resetIndices()
        adjacencyTable = createAdjacencyTable()
        setLevels()
        findObjectAdditions()
        findAttributeAdditions()
    }

    private fun resetIndices() {
        val indexesMap = HashMap<Int, Int>()

        nodes.forEachIndexed { index, node -> indexesMap[node.id] = index }
        nodes.forEach { node -> node.id = indexesMap[node.id]!! }
        links.forEach { link ->
            link.source = indexesMap[link.source]!!
            link.target = indexesMap[link.target]!!
        }
    }

    private fun createAdjacencyTable(): AdjacencyTable {
        val table = AdjacencyTable(nodes.map { node -> node.id })
        links.forEach { table.addUndirectedLink(it.source, it.target) }

        return table
    }

    private fun setLevels() {
        val queue = PriorityQueue<Int>()

        val maxExtentLength = nodes.map { it.extent?.size ?: 0 }.maxOrNull()
        val firstNodeIndex = nodes
            .indexOfFirst { (it.extent?.size ?: 0) == maxExtentLength }

        if (firstNodeIndex == -1) return

        nodes.forEach { it.level = -1 }
        nodes[firstNodeIndex].level = 0

        queue.add(firstNodeIndex)

        while (queue.isNotEmpty()) {
            val nodeIndex = queue.poll()
            adjacencyTable.nodes[nodeIndex!!]
                .adjacentNodes
                .filter { i -> (nodes[i].extent?.size ?: 0) < (nodes[nodeIndex].extent?.size ?: 0) }
                .forEach { i ->
                    nodes[i].level = maxOf(nodes[i].level, nodes[nodeIndex].level + 1)
                    queue.add(i)
                }
        }
    }

    private fun findObjectAdditions() {
        for (nodeIndex in adjacencyTable.nodes.indices) {
            val nodeExtent = nodes[nodeIndex].extent ?: emptyList()
            val totalChildrenExtent = adjacencyTable.nodes[nodeIndex]
                .adjacentNodes
                .map { nodes[it] }
                .filter { it.level > nodes[nodeIndex].level }
                .mapNotNull { it.extent?.sorted() }
                .fold(emptyList<String>()) { left, right -> mergeSortedLists(left, right) }

            nodes[nodeIndex].newObjectAdded = !isSubset(nodeExtent, totalChildrenExtent)
        }
    }

    private fun findAttributeAdditions() {
        for (nodeIndex in adjacencyTable.nodes.indices) {
            val nodeIntent = nodes[nodeIndex].intent ?: emptyList()
            val totalChildrenIntent = adjacencyTable.nodes[nodeIndex]
                .adjacentNodes
                .map { nodes[it] }
                .filter { it.level < nodes[nodeIndex].level }
                .mapNotNull { it.intent?.sorted() }
                .fold(emptyList<String>()) { left, right -> mergeSortedLists(left, right) }

            nodes[nodeIndex].newAttributeAdded = !isSubset(nodeIntent, totalChildrenIntent)
        }
    }

    private fun isSubset(left: List<String>, right: List<String>): Boolean {
        return (left.size < right.size ||
                left.all {
                    right.contains(it)
                })
    }

    private fun mergeSortedLists(left: List<String>, right: List<String>): List<String> {
        val result = ArrayList<String>()
        val leftIterator = left.iterator()
        val rightIterator = right.iterator()

        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            val nextLeft = leftIterator.next()
            val nextRight = rightIterator.next()
            when {
                nextLeft > nextRight -> {
                    result.add(nextRight)
                    result.add(nextLeft)
                }

                nextLeft < nextRight -> {
                    result.add(nextLeft)
                    result.add(nextRight)
                }

                else -> result.add(nextLeft)
            }
        }

        while (leftIterator.hasNext())
            result.add(leftIterator.next())

        while (rightIterator.hasNext())
            result.add(rightIterator.next())

        return result
    }
}