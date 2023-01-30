package com.fca.fcamobile.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fca.fcamobile.model.GraphSettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GraphSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val settingsFlow: Flow<GraphSettingsModel> = dataStore.data
        .map {
            val viewMode = GraphSettingsModel.GraphViewMode.fromString(
                it[PreferencesKeys.VIEW_MODE_VALUE] ?: GraphSettingsModel.GraphViewMode.FORCE_GRAPH.toString()
            )
            val currentNodeId =
                if (it[PreferencesKeys.CURRENT_NODE_ID] == CURRENT_NODE_NULL_STRING) null
                else it[PreferencesKeys.CURRENT_NODE_ID] ?.toInt()

            GraphSettingsModel(viewMode, currentNodeId)
        }

    suspend fun setGraphSettings(settings: GraphSettingsModel) = dataStore.edit {
        it[PreferencesKeys.VIEW_MODE_VALUE] = settings.viewMode.toString()
        it[PreferencesKeys.CURRENT_NODE_ID] = settings.currentNodeId?.toString() ?: CURRENT_NODE_NULL_STRING
    }

    private object PreferencesKeys {
        val VIEW_MODE_VALUE = stringPreferencesKey("viewModeValue")
        val CURRENT_NODE_ID = stringPreferencesKey("currentNodeValue")
    }

    companion object {
        private const val CURRENT_NODE_NULL_STRING = "null"
    }
}