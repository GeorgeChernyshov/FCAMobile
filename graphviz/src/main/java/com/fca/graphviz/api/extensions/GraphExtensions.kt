package com.fca.graphviz.api.extensions

import android.util.Log
import com.fca.graphviz.api.interactor.FilterInteractor
import com.fca.graphviz.entities.AdjacencyMatrix
import com.fca.graphviz.entities.AdjacencyMatrix.Companion.NO_LINK
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Link
import com.fca.graphviz.entities.Node
import kotlin.system.measureTimeMillis

fun Graph.filter(predicate: (Node) -> Boolean): Graph {
    lateinit var newGraph: Graph
    val time = measureTimeMillis {
        newGraph = FilterInteractor(this, predicate).invoke()
    }

    Log.println(Log.INFO, "TimerMessage","Filter time elapsed: $time")

    return newGraph
}