package com.fca.fcapstographviz.entities

data class Graph(
    val graphInfo: GraphInfo,
    val nodes: List<Node>,
    val arcs: List<Arc>
)