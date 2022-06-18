package com.fca.fcamobile.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fca.graphviz.entities.Graph

class GraphViewModel : ViewModel() {

    private val _graph = MutableLiveData<Graph?>()
    val graph: LiveData<Graph?> = _graph

    fun setGraph(graph: Graph?) {
        _graph.postValue(graph)
    }
}