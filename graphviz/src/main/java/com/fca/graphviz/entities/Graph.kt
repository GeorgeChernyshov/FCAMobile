package com.fca.graphviz.entities

import com.fca.graphviz.api.interactor.TopSortInteractor
import java.util.*

data class Graph(
    val nodes: MutableList<Node>,
    val links: List<Link>
) {

    var directedAdjacencyTable: AdjacencyTable
        private set

    init {
        // join indices to make them 0..graph.size
        resetIndices()
        // create undirected adjacency table
        val undirectedAdjacencyTable = createUndirectedAdjacencyTable()
        // set levels
        setLevels(undirectedAdjacencyTable)
        // rearrange indices in topsort order
        val newIndices = TopSortInteractor(this, undirectedAdjacencyTable).invoke()
        for (i in nodes.indices)
            nodes[i].id = newIndices[i]
        nodes.sortBy { node -> node.id }

        links.forEach {
            it.source = newIndices[it.source]
            it.target = newIndices[it.target]
        }
        //create directed adjacency table
        directedAdjacencyTable = createDirectedAdjacencyTable()
    }

    fun init() {
        // join indices to make them 0..graph.size
        resetIndices()
        // create undirected adjacency table
        val undirectedAdjacencyTable = createUndirectedAdjacencyTable()
        // set levels
        setLevels(undirectedAdjacencyTable)
        // rearrange indices in topsort order
        val newIndices = TopSortInteractor(this, undirectedAdjacencyTable).invoke()
        for (i in nodes.indices)
            nodes[i].id = newIndices[i]
        nodes.sortBy { node -> node.id }

        links.forEach {
            it.source = newIndices[it.source]
            it.target = newIndices[it.target]
        }
        //create directed adjacency table
        directedAdjacencyTable = createDirectedAdjacencyTable()
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

    private fun createUndirectedAdjacencyTable(): AdjacencyTable {
        val table = AdjacencyTable(nodes.map { node -> node.id })
        links.forEach { table.addUndirectedLink(it.source, it.target) }

        return table
    }

    private fun createDirectedAdjacencyTable(): AdjacencyTable {
        val table = AdjacencyTable(nodes.map { node -> node.id })
        links.forEach {
            if (nodes[it.source].level < nodes[it.target].level)
                table.addDirectedLink(it.source, it.target)
            else table.addDirectedLink(it.target, it.source)
        }

        return table
    }

    private fun setLevels(adjacencyTable: AdjacencyTable) {
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