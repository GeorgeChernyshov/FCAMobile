package com.fca.fcamobile.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.fca.fcamobile.model.FiltersModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val filtersFlow: Flow<FiltersModel> = dataStore.data
        .map {
            val stabFilterValueString = it[PreferencesKeys.STAB_FILTER_VALUE] ?: DEFAULT_FILTER_STRING
            val deltaFilterValueString = it[PreferencesKeys.DELTA_FILTER_VALUE] ?: DEFAULT_FILTER_STRING
            val impactFilterValueString = it[PreferencesKeys.IMPACT_FILTER_VALUE] ?: DEFAULT_FILTER_STRING
            val pvalueFilterValueString = it[PreferencesKeys.P_VALUE_FILTER_VALUE] ?: DEFAULT_FILTER_STRING
            val stabFilterEnabled = it[PreferencesKeys.STAB_FILTER_ENABLED] ?: false
            val deltaFilterEnabled = it[PreferencesKeys.DELTA_FILTER_ENABLED] ?: false
            val impactFilterEnabled = it[PreferencesKeys.IMPACT_FILTER_ENABLED] ?: false
            val pvalueFilterEnabled = it[PreferencesKeys.P_VALUE_FILTER_ENABLED] ?: false
            FiltersModel(
                stabFilterValueString.toBigDecimal(),
                deltaFilterValueString.toBigDecimal(),
                impactFilterValueString.toBigDecimal(),
                pvalueFilterValueString.toBigDecimal(),
                stabFilterEnabled,
                deltaFilterEnabled,
                impactFilterEnabled,
                pvalueFilterEnabled
            )
        }

    suspend fun setFilter(model: FiltersModel) = dataStore.edit {
        it[PreferencesKeys.STAB_FILTER_VALUE] = model.stabFilterValue.toString()
        it[PreferencesKeys.DELTA_FILTER_VALUE] = model.deltaFilterValue.toString()
        it[PreferencesKeys.IMPACT_FILTER_VALUE] = model.impactFilterValue.toString()
        it[PreferencesKeys.P_VALUE_FILTER_VALUE] = model.pvalueFilterValue.toString()
        it[PreferencesKeys.STAB_FILTER_ENABLED] = model.stabFilterEnabled
        it[PreferencesKeys.DELTA_FILTER_ENABLED] = model.deltaFilterEnabled
        it[PreferencesKeys.IMPACT_FILTER_ENABLED] = model.impactFilterEnabled
        it[PreferencesKeys.P_VALUE_FILTER_ENABLED] = model.pvalueFilterEnabled
    }

    private object PreferencesKeys {
        val STAB_FILTER_VALUE = stringPreferencesKey("stabFilterValue")
        val DELTA_FILTER_VALUE = stringPreferencesKey("deltaFilterValue")
        val IMPACT_FILTER_VALUE = stringPreferencesKey("impactFilterValue")
        val P_VALUE_FILTER_VALUE = stringPreferencesKey("pvalueFilterValue")
        val STAB_FILTER_ENABLED = booleanPreferencesKey("stabFilterEnabled")
        val DELTA_FILTER_ENABLED = booleanPreferencesKey("deltaFilterEnabled")
        val IMPACT_FILTER_ENABLED = booleanPreferencesKey("impactFilterEnabled")
        val P_VALUE_FILTER_ENABLED = booleanPreferencesKey("pvalueFilterEnabled")
    }

    companion object {
        val FILTER_PREFERENCES = "filterPreferences"

        private const val DEFAULT_FILTER_STRING = "0.00"
    }
}