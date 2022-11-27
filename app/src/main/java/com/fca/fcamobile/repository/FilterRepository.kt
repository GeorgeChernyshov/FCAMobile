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
            val impactFilterValueString = it[PreferencesKeys.IMPACT_FILTER_VALUE] ?: DEFAULT_FILTER_STRING
            val stabFilterEnabled = it[PreferencesKeys.STAB_FILTER_ENABLED] ?: false
            val impactFilterEnabled = it[PreferencesKeys.IMPACT_FILTER_ENABLED] ?: false
            FiltersModel(
                stabFilterValueString.toBigDecimal(),
                impactFilterValueString.toBigDecimal(),
                stabFilterEnabled,
                impactFilterEnabled
            )
        }

    suspend fun setFilter(model: FiltersModel) = dataStore.edit {
        it[PreferencesKeys.STAB_FILTER_VALUE] = model.stabFilterValue.toString()
        it[PreferencesKeys.IMPACT_FILTER_VALUE] = model.impactFilterValue.toString()
        it[PreferencesKeys.STAB_FILTER_ENABLED] = model.stabFilterEnabled
        it[PreferencesKeys.IMPACT_FILTER_ENABLED] = model.impactFilterEnabled
    }

    private object PreferencesKeys {
        val STAB_FILTER_VALUE = stringPreferencesKey("stabFilterValue")
        val IMPACT_FILTER_VALUE = stringPreferencesKey("impactFilterValue")
        val STAB_FILTER_ENABLED = booleanPreferencesKey("stabFilterEnabled")
        val IMPACT_FILTER_ENABLED = booleanPreferencesKey("impactFilterEnabled")
    }

    companion object {
        val FILTER_PREFERENCES = "filterPreferences"

        private const val DEFAULT_FILTER_STRING = "0.00"
    }
}