package com.fca.graphviz.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import java.lang.Exception

class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val webView = WebView(context, attrs, defStyleRes)

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

            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            loadUrl(DEFAULT_URL)
        }
    }

    companion object {
        private const val DEFAULT_URL = "file:///android_asset/com/fca/graphviz/index.html"
    }
}