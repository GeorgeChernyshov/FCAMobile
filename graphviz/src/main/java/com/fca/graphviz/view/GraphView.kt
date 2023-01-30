package com.fca.graphviz.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.fca.graphviz.api.interfaces.ClickListenerInterface
import com.fca.graphviz.api.interfaces.DragListenerInterface
import com.fca.graphviz.api.serializer.GsonProvider
import com.fca.graphviz.entities.BridgeMessage
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@SuppressLint("RequiresFeature")
class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val webView = WebView(context, attrs, defStyleRes)

    private val webMessageFlow = MutableStateFlow<String?>(null)

    private val serializer = GsonProvider.newInstance()

    private val dragListenerInterface = DragListenerInterface()
    private val clickListenerInterface = ClickListenerInterface(serializer)

    init {
        addView(webView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        with (webView) {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
            }

            addJavascriptInterface(dragListenerInterface, DragListenerInterface.JS_NAME)
            addJavascriptInterface(clickListenerInterface, ClickListenerInterface.JS_NAME)

            loadUrl(DEFAULT_URL)
        }

        webView.webViewClient = (object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                startWebMessaging()
            }
        })
    }

    fun onDragStarted(func: (() -> Unit)) = apply {
        dragListenerInterface.onDragStart = func
    }

    fun onDragEnded(func: (() -> Unit)) = apply {
        dragListenerInterface.onDragEnd = func
    }

    fun onNodeClicked(func: (Node) -> Unit) = apply {
        clickListenerInterface.onNodeClick = func
    }

    private fun startWebMessaging() {
        CoroutineScope(Dispatchers.Main).launch {
            getMessage()
        }
    }

    private suspend fun getMessage() {
        webMessageFlow.collect {
            it?.let { webView.evaluateJavascript(it) {} }
        }
    }

    fun setGraph(graph: Graph) {
        CoroutineScope(Dispatchers.IO).launch {
            val message = BridgeMessage(SET_GRAPH, mapOf(GRAPH to graph))
            val jsonMessage = serializer.toJson(message)
            webMessageFlow.emit("parseWebChannelMessage($jsonMessage);")
        }
    }

    fun setMode(mode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val message = BridgeMessage(SET_MODE, mapOf(MODE to mode))
            val jsonMessage = serializer.toJson(message)
            webMessageFlow.emit("parseWebChannelMessage($jsonMessage);")
        }
    }

    companion object {
        private const val DEFAULT_URL = "file:///android_asset/com/fca/graphviz/index.html"
        const val SET_GRAPH = "setGraph"
        const val SET_MODE = "setMode"
        const val GRAPH = "graph"
        const val MODE = "mode"
    }
}