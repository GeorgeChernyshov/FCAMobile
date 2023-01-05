package com.fca.graphviz.api.interactor

import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Link
import com.fca.graphviz.entities.Node
import java.util.LinkedList

class FilterInteractor(
    private val graph: Graph,
    private val predicate: (Node) -> Boolean
) {

    val potentialLinks = Array(graph.nodes.size) {
        LinkedList<Int>()
    }

    fun invoke(): Graph {
        val filteredNodes = ArrayList<Node>()
        val filteredLinks = ArrayList<Link>()
        val firstIndex = graph.nodes.first { it.level == 0 }.id
        filterNode(filteredNodes, filteredLinks, firstIndex)
        return Graph(filteredNodes, filteredLinks)
    }

    private fun filterNode(
        nodes: MutableList<Node>,
        links: MutableList<Link>,
        index: Int
    ): Boolean {
        for (i in graph.adjacencyTable
            .nodes[index]
            .adjacentNodes
            .filter { graph.nodes[it].level > graph.nodes[index].level }
        ) {
            if (filterNode(nodes, links, i))
                links.add(Link(index, i, 1))

            potentialLinks[index].addAll(potentialLinks[i])
        }

        if (predicate.invoke(graph.nodes[index])) {
            nodes.add(graph.nodes[index].copy())
        }

        return true
    }

    private fun filterSubsetSet(set: LinkedList<Int>): LinkedList<Int> {
        val result = set
        for (i in 0 until result.size) {
            for (j in i + 1 until result.size) {
                if (isExtentSubset(graph.nodes[j], graph.nodes[i]))
                    result.remove(j)
            }
        }

        return result
    }

    private fun isExtentSubset(left: Node, right: Node): Boolean {
        if (left.level >= right.level) return false

        if (left.extent == null || right.extent == null) return true

        val leftExtentIterator = left.extent.sorted().iterator()
        val rightExtentIterator = right.extent.sorted().iterator()

        while (rightExtentIterator.hasNext()) {
            val attrToSearch = rightExtentIterator.next()
            var currentAttr = leftExtentIterator.next()
            while (currentAttr != attrToSearch) {
                if (!leftExtentIterator.hasNext()) return false
                currentAttr = leftExtentIterator.next()
            }
        }

        return true
    }
}