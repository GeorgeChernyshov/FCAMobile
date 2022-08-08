package com.fca.graphviz.entities

data class Node(
    val id: String,
    val group: Int,
    val level: Int,
    val extent: String? = null,
    val intent: String? = null
)