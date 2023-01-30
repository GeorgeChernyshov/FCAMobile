package com.fca.fcamobile.model

import com.fca.graphviz.entities.Node

data class GraphSettingsModel(
    val viewMode: GraphViewMode = GraphViewMode.FORCE_GRAPH,
    val currentNodeId: Int? = null
) {
    enum class GraphViewMode {
        FORCE_GRAPH, NODE_TRAVERSAL;

        companion object {
            fun fromString(value: String): GraphViewMode = when (value) {
                NODE_TRAVERSAL.toString() -> NODE_TRAVERSAL
                else -> FORCE_GRAPH
            }
        }
    }
}