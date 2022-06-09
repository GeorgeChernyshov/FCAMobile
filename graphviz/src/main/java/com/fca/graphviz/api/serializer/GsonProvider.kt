package com.fca.graphviz.api.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonProvider {
    fun newInstance(): Gson {
        return GsonBuilder().create()
    }
}