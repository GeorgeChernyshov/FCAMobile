package com.fca.fcapstographviz.entities

internal data class Node(
    val extent: Extent,
    val lStab: Double? = null,
    val uStab: Int? = null,
    val stab: Double = STAB_INF,
    val intent: Intent?
) {
    companion object {
        const val STAB_INF = -1.0
    }
}