package com.fca.fcamobile.ui.viewmodels

import androidx.lifecycle.*
import com.fca.fcamobile.model.FiltersModel
import com.fca.fcamobile.model.GraphSettingsModel
import com.fca.fcamobile.model.GraphSettingsModel.GraphViewMode
import com.fca.fcamobile.model.GraphSettingsModel.GraphViewMode.FORCE_GRAPH
import com.fca.fcamobile.model.GraphSettingsModel.GraphViewMode.NODE_TRAVERSAL
import com.fca.fcamobile.repository.FilterRepository
import com.fca.fcamobile.repository.GraphRepository
import com.fca.fcamobile.repository.GraphSettingsRepository
import com.fca.fcamobile.ui.state.GraphUiState
import com.fca.graphviz.api.extensions.filter
import com.fca.graphviz.entities.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.BufferedReader
import javax.inject.Inject

@HiltViewModel
class FCAViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val graphRepository: GraphRepository,
    private val filterRepository: FilterRepository,
    private val graphSettingsRepository: GraphSettingsRepository
) : ViewModel() {

    private val _graph = MutableStateFlow<Graph?>(null)
    val graph: StateFlow<Graph?> = _graph

    private val _graphUiState = MutableLiveData<GraphUiState>()
    val graphUiState: LiveData<GraphUiState> = _graphUiState

    private val _graphViewMode = MutableLiveData<GraphViewMode>()
    val graphViewMode: LiveData<GraphViewMode> = _graphViewMode

    val filters = filterRepository.filtersFlow.asLiveData()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    init {
        uiScope.launch {
            graphRepository.graphStream.collect { setGraph(it) }
        }

        uiScope.launch {
            graphSettingsRepository.settingsFlow.collect {
                _graphViewMode.postValue(it.viewMode)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setGraph(graph: Graph?) = viewModelScope.launch {
        val resetFilters = _graph.value != null
        _graph.emit(graph)
        if (resetFilters)
            filterRepository.setFilter(FiltersModel.Default)

        applyFilter(
            filterRepository.filtersFlow
                .asLiveData(coroutineContext)
                .value ?: FiltersModel.Default
        )
    }

    fun importGraphFrom(jsonReader: BufferedReader) = uiScope.launch {
        graphRepository.importGraphFrom(jsonReader)
    }

    fun applyFilter(model: FiltersModel) = viewModelScope.launch {
        with (model) {
            val filteredGraph = if (
                stabFilterEnabled ||
                deltaFilterEnabled ||
                impactFilterEnabled ||
                pvalueFilterEnabled
            ) {
                graph.value?.filter(
                    if (stabFilterEnabled) stabFilterValue.toDouble() else null,
                    if (deltaFilterEnabled) deltaFilterValue.toInt() else null,
                    if (impactFilterEnabled) impactFilterValue.toDouble() else null,
                    if (pvalueFilterEnabled) pvalueFilterValue.toDouble() else null
                )
            } else graph.value

            _graphUiState.postValue(GraphUiState(filteredGraph, null))
        }
    }

    suspend fun setFilter(model: FiltersModel) = filterRepository.setFilter(model)

    suspend fun switchMode() {
        val newMode = when (graphViewMode.value) {
            NODE_TRAVERSAL -> FORCE_GRAPH
            else -> NODE_TRAVERSAL
        }

        graphSettingsRepository.setGraphSettings(GraphSettingsModel(newMode))
    }
}