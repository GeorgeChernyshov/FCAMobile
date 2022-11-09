package com.fca.fcamobile.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fca.fcamobile.R
import com.fca.fcamobile.databinding.ViewNodeInfoBinding
import com.fca.graphviz.entities.Node

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

    fun setNode(node: Node) {
        binding.extentTextView.text = context.getString(R.string.info_extent, node.extent?.joinToString(", "))
        binding.intentTextView.text = context.getString(R.string.info_intent, node.intent?.joinToString(", "))
    }
}