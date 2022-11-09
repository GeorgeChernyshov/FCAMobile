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
    }

    fun init() {
        resetIndices()
        adjacencyTable = createAdjacencyTable()
        setLevels()
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
}