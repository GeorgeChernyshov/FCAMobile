package com.fca.fcamobile.ui.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class AssistedInputFilterViewModel(val coroutineScope: CoroutineScope) {
    private val _textFlow = MutableStateFlow<String?>(null)
    val textFlow: StateFlow<String?> = _textFlow

    fun setText(text: String?) {
        if (text == textFlow.value) return
        if (text?.firstOrNull() == '.' || text?.lastOrNull() == '.' || text.isNullOrEmpty()) return

        var decimalValue = text.toString().toBigDecimal()

        decimalValue = when {
            decimalValue < BigDecimal.ZERO -> BigDecimal.ZERO
            decimalValue > BigDecimal.ONE -> BigDecimal.ONE
            else -> decimalValue.setScale(2, RoundingMode.CEILING)
        }

        coroutineScope.launch {
            _textFlow.emit(decimalValue.toString())
        }
    }
}