package com.fca.fcapstographviz.entities

internal data class Node(
    val extent: Extent,
    val intent: Intent?,
    val lStab: Double? = null,
    val uStab: Int = USTAB_DEFAULT,
    val stab: Double = STAB_INF,
    val pValue: Double = P_VALUE_DEFAULT,
    val impact: Double = IMPACT_DEFAULT
) {
    companion object {
        const val STAB_INF = -1.0
        const val USTAB_DEFAULT = 0
        const val P_VALUE_DEFAULT = 0.0
        const val IMPACT_DEFAULT = 0.0
    }
}