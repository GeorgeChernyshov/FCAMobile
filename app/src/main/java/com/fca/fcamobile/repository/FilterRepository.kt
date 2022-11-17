package com.fca.fcamobile.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.fca.fcamobile.model.FiltersModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val filtersFlow: Flow<FiltersModel> = dataStore.data
        .map {
            val stabFilterEnabled = it[PreferencesKeys.STAB_FILTER_ENABLED] ?: false
            val impactFilterEnabled = it[PreferencesKeys.IMPACT_FILTER_ENABLED] ?: false
            FiltersModel(
                0.0,
                0.0,
                stabFilterEnabled,
                impactFilterEnabled
            )
        }

    suspend fun setFilter(model: FiltersModel) = dataStore.edit {
        it[PreferencesKeys.STAB_FILTER_ENABLED] = model.stabFilterEnabled
        it[PreferencesKeys.IMPACT_FILTER_ENABLED] = model.impactFilterEnabled
    }

    private object PreferencesKeys {
        val STAB_FILTER_ENABLED = booleanPreferencesKey("stabFilterEnabled")
        val IMPACT_FILTER_ENABLED = booleanPreferencesKey("impactFilterEnabled")
    }

    companion object {
        val FILTER_PREFERENCES = "filterPreferences"
    }
}