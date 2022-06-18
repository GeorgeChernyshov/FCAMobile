package com.fca.fcapstographviz.deserializer

import com.fca.fcapstographviz.entities.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal object GsonProvider {
    fun newInstance(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Arc::class.java, ArcDeserializer())
            .registerTypeAdapter(Intent::class.java, IntentDeserializer())
            .registerTypeAdapter(Node::class.java, NodeDeserializer())
            .registerTypeAdapter(GraphInfo::class.java, GraphInfoDeserializer())
            .registerTypeAdapter(Graph::class.java, GraphDeserializer())
            .create()
    }
}