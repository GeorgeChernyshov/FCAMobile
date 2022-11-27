package com.fca.fcamobile.model

import java.math.BigDecimal

data class FiltersModel(
    val stabFilterValue: BigDecimal,
    val impactFilterValue: BigDecimal,
    val stabFilterEnabled: Boolean,
    val impactFilterEnabled: Boolean
    ) {

    companion object {
        val Default = FiltersModel(
            stabFilterValue = BigDecimal.ZERO,
            impactFilterValue = BigDecimal.ZERO,
            stabFilterEnabled = false,
            impactFilterEnabled = false
        )
    }
}