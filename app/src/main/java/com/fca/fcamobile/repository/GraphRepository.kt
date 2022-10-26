package com.fca.fcamobile.repository

import com.fca.fcamobile.datasource.GraphLocalDataSource
import com.fca.graphviz.entities.Graph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.BufferedReader
import javax.inject.Inject

class GraphRepository @Inject constructor(
    private val localDataSource: GraphLocalDataSource,
    private val externalScope: CoroutineScope
) {
    val graphStream = MutableStateFlow<Graph?>(null)

    init {
        externalScope.launch {
            graphStream.emit(localDataSource.getGraph())
            localDataSource.graphChangedEvent.collect { graphChanged ->
                if (graphChanged) {
                    graphStream.emit(localDataSource.getGraph())
                    localDataSource.graphChangedEvent.emit(false)
                }
            }
        }
    }

    suspend fun getGraph() : Graph = externalScope.async { localDataSource.getGraph() }.await()

    suspend fun importGraphFrom(jsonReader: BufferedReader) = externalScope.launch {
        localDataSource.importGraphFrom(jsonReader)
    }
}