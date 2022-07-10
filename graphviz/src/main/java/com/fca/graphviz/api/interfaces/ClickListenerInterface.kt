package com.fca.graphviz.api.interfaces

import android.webkit.JavascriptInterface
import com.fca.graphviz.entities.Node

class ClickListenerInterface {
    var onNodeClick: ((String) -> Unit) = {}

    @JavascriptInterface
    fun onNodeClicked(node: String) = onNodeClick.invoke(node)

    companion object {
        const val JS_NAME = "ClickListener"
    }
}