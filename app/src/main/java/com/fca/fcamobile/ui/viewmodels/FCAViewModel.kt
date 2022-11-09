package com.fca.fcamobile.ui.viewmodels

import androidx.lifecycle.*
import com.fca.fcamobile.repository.FilterRepository
import com.fca.fcamobile.repository.GraphRepository
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
    private val filterRepository: FilterRepository
) : ViewModel() {

    private val _graph = MutableStateFlow<Graph?>(null)
    val graph: StateFlow<Graph?> = _graph

    private val _graphUiState = MutableLiveData<GraphUiState>()
    val graphUiState: LiveData<GraphUiState> = _graphUiState

    val filters = filterRepository.filtersFlow.asLiveData()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    init {
        uiScope.launch {
            graphRepository.graphStream.collect { setGraph(it) }
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
            filterRepository.setStabFilter(false)

        applyFilter(
            filterRepository.filtersFlow
                .asLiveData(coroutineContext)
                .value?.stabFilter
                ?: false
        )
    }

    fun importGraphFrom(jsonReader: BufferedReader) = uiScope.launch {
        graphRepository.importGraphFrom(jsonReader)
    }

    fun applyFilter(enabled: Boolean) = viewModelScope.launch {
        val filteredGraph = if (enabled) {
            graph.value?.filter { node -> node.stab > 0.5 }
        } else graph.value

        _graphUiState.postValue(GraphUiState(filteredGraph))
    }

    suspend fun setFilter(enabled: Boolean) = filterRepository.setStabFilter(enabled)
}