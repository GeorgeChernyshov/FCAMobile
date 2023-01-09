package com.fca.fcamobile.model

import java.math.BigDecimal

data class FiltersModel(
    val stabFilterValue: BigDecimal,
    val deltaFilterValue: BigDecimal,
    val impactFilterValue: BigDecimal,
    val pvalueFilterValue: BigDecimal,
    val stabFilterEnabled: Boolean,
    val deltaFilterEnabled: Boolean,
    val impactFilterEnabled: Boolean,
    val pvalueFilterEnabled: Boolean
    ) {

    companion object {
        val Default = FiltersModel(
            stabFilterValue = BigDecimal.ZERO,
            deltaFilterValue = BigDecimal.ZERO,
            impactFilterValue = BigDecimal.ZERO,
            pvalueFilterValue = BigDecimal.ZERO,
            stabFilterEnabled = false,
            deltaFilterEnabled = false,
            impactFilterEnabled = false,
            pvalueFilterEnabled = false
        )
    }
}