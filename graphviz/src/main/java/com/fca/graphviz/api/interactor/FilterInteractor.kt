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

    val filterResult = Array<Boolean?>(graph.nodes.size) { null }

    fun invoke(): Graph {
        val filteredNodes = ArrayList<Node>()
        val filteredLinks = ArrayList<Link>()
        filterNode(filteredNodes, filteredLinks, 0)
        return Graph(filteredNodes, filteredLinks)
    }

    private fun filterNode(
        nodes: MutableList<Node>,
        links: MutableList<Link>,
        index: Int
    ) {
        filterResult[index] = predicate.invoke(graph.nodes[index])
        val adjacentNodes = graph.directedAdjacencyTable
            .nodes[index]
            .adjacentNodes

        for (i in adjacentNodes) {
            if (filterResult[i] == null)
                filterNode(nodes, links, i)

            potentialLinks[index].addAll(potentialLinks[i])
        }

        if (filterResult[index] == true) {
            nodes.add(graph.nodes[index].copy())

            for (i in removeSubsetSet(potentialLinks[index]))
                links.add(Link(index, i, 1))

            potentialLinks[index] = LinkedList<Int>().apply {
                add(index)
            }
        }
    }

    private fun removeSubsetSet(initList: LinkedList<Int>): LinkedList<Int> {
        val result = LinkedList<Int>() 
        result.addAll(initList.sorted().distinct())
        for (i in result.indices) {
            var j = i + 1
            while (j < result.size) {
                if (isExtentSubset(graph.nodes[result[j]], graph.nodes[result[i]]))
                    result.removeAt(j--)

                j++
            }
        }

        return result
    }

    private fun isExtentSubset(left: Node, right: Node): Boolean {
        if (left.id == 2) {
            val a = 0
        }
        if (left.level <= right.level) return false

        if (left.extent == null || right.extent == null) return true

        val leftExtentIterator = left.extent.sorted().iterator()
        val rightExtentIterator = right.extent.sorted().iterator()

        while (leftExtentIterator.hasNext()) {
            val attrToSearch = leftExtentIterator.next()
            if (!rightExtentIterator.hasNext()) return false
            var currentAttr = rightExtentIterator.next()
            while (currentAttr != attrToSearch) {
                if (!rightExtentIterator.hasNext()) return false
                currentAttr = rightExtentIterator.next()
            }
        }

        return true
    }
}