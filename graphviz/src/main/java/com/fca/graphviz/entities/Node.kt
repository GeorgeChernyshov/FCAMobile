package com.fca.graphviz.entities

data class Node(
    var id: Int,
    val group: Int,
    var level: Int,
    val extent: List<String>? = null,
    val intent: List<String>? = null,
    val stab: Double = 0.0
)