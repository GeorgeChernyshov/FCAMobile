package com.fca.fcamobile.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fca.fcamobile.R
import com.fca.fcamobile.databinding.ViewNodeInfoBinding

class NodeInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var binding: ViewNodeInfoBinding

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_node_info, this)
        } else {
            binding = ViewNodeInfoBinding.inflate(LayoutInflater.from(context), this, true)
        }
    }
}