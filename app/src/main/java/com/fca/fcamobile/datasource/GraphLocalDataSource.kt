package com.fca.fcamobile.datasource

import android.content.Context
import android.util.JsonReader
import com.fca.fcapstographviz.api.FcapsUtils
import com.fca.graphviz.api.serializer.GsonProvider
import com.fca.graphviz.entities.Graph
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class GraphLocalDataSource @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val file: File
) {
    private val serializer = GsonProvider.newInstance()

    val graphChangedEvent = MutableStateFlow(false)

    suspend fun getGraph() : Graph = withContext(coroutineDispatcher) {
        try {
            serializer.fromJson(file.readText(), Graph::class.java)
        }
        catch (e: Exception) { Graph(emptyList(), emptyList()) }
    }

    suspend fun importGraphFrom(jsonReader: BufferedReader) = withContext(coroutineDispatcher) {
        val graph = FcapsUtils.getGraph(jsonReader)
        file.writeText(serializer.toJson(graph))
        graphChangedEvent.emit(true)
    }
}