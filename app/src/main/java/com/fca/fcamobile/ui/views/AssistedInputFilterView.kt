package com.fca.fcamobile.ui.views

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fca.fcamobile.R
import com.fca.fcamobile.databinding.ViewFilterAssistedInputBinding
import java.util.regex.Pattern

class AssistedInputFilterView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var binding: ViewFilterAssistedInputBinding

    var isChecked
        get() = binding.filterSwitch.isChecked
        set(value) { binding.filterSwitch.isChecked = value }

    val switch get() = binding.filterSwitch

    fun setTitle(title: String) {
        binding.titleTextView.text = title
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_filter_assisted_input, this)
        } else {
            binding = ViewFilterAssistedInputBinding
                .inflate(LayoutInflater.from(context), this, true)
           // binding.inputCell
        }
    }
}