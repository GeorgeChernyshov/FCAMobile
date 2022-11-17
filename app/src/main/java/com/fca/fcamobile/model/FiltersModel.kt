package com.fca.fcamobile.model

data class FiltersModel(
    val stabFilterValue: Double,
    val impactFilterValue: Double,
    val stabFilterEnabled: Boolean,
    val impactFilterEnabled: Boolean
    ) {

    companion object {
        val Default = FiltersModel(
            stabFilterValue = 0.0,
            impactFilterValue = 0.0,
            stabFilterEnabled = false,
            impactFilterEnabled = false
        )
    }
}