package com.fca.graphviz.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.webkit.WebMessageCompat
import androidx.webkit.WebViewCompat
import com.fca.graphviz.api.interfaces.ClickListenerInterface
import com.fca.graphviz.api.interfaces.DragListenerInterface
import com.fca.graphviz.api.serializer.GsonProvider
import com.fca.graphviz.entities.BridgeMessage
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

@SuppressLint("RequiresFeature")
class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val webView = WebView(context, attrs, defStyleRes)

    private val webMessageChannel = Channel<String>(10)

    private val serializer = GsonProvider.newInstance()

    private val dragListenerInterface = DragListenerInterface()
    private val clickListenerInterface = ClickListenerInterface()

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

    fun onNodeClicked(func: (String) -> Unit) = apply {
        clickListenerInterface.onNodeClick = func
    }

    private fun startWebMessaging() {
        CoroutineScope(Dispatchers.Main).launch {
            getMessage(this)
        }
    }

    private suspend fun getMessage(scope: CoroutineScope) {
        webView.evaluateJavascript(webMessageChannel.receive()) {
            scope.launch {
                getMessage(scope)
            }
        }
    }

    fun setGraph(graph: Graph) {
        CoroutineScope(Dispatchers.IO).launch {
            val message = BridgeMessage(SET_GRAPH, mapOf(GRAPH to graph))
            val jsonMessage = serializer.toJson(message)
            webMessageChannel.send("parseWebChannelMessage($jsonMessage);")
        }
    }

    companion object {
        private const val DEFAULT_URL = "file:///android_asset/com/fca/graphviz/index.html"
        const val SET_GRAPH = "setGraph"
        const val GRAPH = "graph"
    }
}