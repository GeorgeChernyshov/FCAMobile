package com.fca.graphviz.api.interfaces

import android.webkit.JavascriptInterface
import com.fca.graphviz.entities.Node
import com.google.gson.Gson

class ClickListenerInterface(val serializer: Gson) {
    var onNodeClick: ((Node) -> Unit) = {}

    @JavascriptInterface
    fun onNodeClicked(node: String) {
        onNodeClick.invoke(serializer.fromJson(node, Node::class.java))
    }

    companion object {
        const val JS_NAME = "ClickListener"
    }
}