package com.fca.fcamobile.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fca.fcamobile.repository.GraphRepository
import com.fca.graphviz.entities.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import javax.inject.Inject

@HiltViewModel
class FCAViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val graphRepository: GraphRepository
) : ViewModel() {

    private val _graph = MutableLiveData<Graph?>()
    val graph: LiveData<Graph?> = _graph

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    init {
        uiScope.launch {
            graphRepository.graphStream.collect {
                setGraph(it)
            }
        }
    }

    fun setGraph(graph: Graph?) {
        _graph.postValue(graph)
    }

    fun importGraphFrom(jsonReader: BufferedReader) = uiScope.launch {
        graphRepository.importGraphFrom(jsonReader)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}