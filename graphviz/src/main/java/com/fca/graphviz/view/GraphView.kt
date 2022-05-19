package com.fca.graphviz.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val DEFAULT_URL = "file:///android_asset/com/fca/graphviz/index.html"
    }
}