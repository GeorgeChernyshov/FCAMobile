package com.fca.fcapstographviz.entities

internal data class GraphInfo(
    val nodesCount: Int,
    val arcsCount: Int,
    val bottom: Int,
    val top: Int
)