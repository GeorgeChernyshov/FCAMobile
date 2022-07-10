package com.fca.graphviz.api.interfaces

import android.webkit.JavascriptInterface

class DragListenerInterface {

    var onDragStart: (() -> Unit) = {}
    var onDragEnd: (() -> Unit) = {}

    @JavascriptInterface
    fun onDragStarted() = onDragStart.invoke()

    @JavascriptInterface
    fun onDragEnded() = onDragEnd.invoke()

    companion object {
        const val JS_NAME = "DragListener"
    }
}