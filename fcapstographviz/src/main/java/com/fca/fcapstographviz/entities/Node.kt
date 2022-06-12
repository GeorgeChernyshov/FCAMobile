package com.fca.fcapstographviz.entities

data class Node(
    val extent: Extent,
    val lStab: Double?,
    val rStab: Int?,
    val stab: Double?,
    val intent: Intent
)