package com.fca.fcamobile.ui.views

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.fca.fcamobile.R
import com.fca.fcamobile.databinding.ViewFilterAssistedInputBinding
import com.fca.fcamobile.ui.viewmodels.AssistedInputFilterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

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
    var inputValue
        get() = binding.inputCell.text.toString().toBigDecimal()
        set(value) { binding.inputCell.setText(value.toString()) }

    private var job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val viewModel = AssistedInputFilterViewModel(coroutineScope)

    fun setTitle(title: String) {
        binding.titleTextView.text = title
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_filter_assisted_input, this)
        } else {
            binding = ViewFilterAssistedInputBinding
                .inflate(LayoutInflater.from(context), this, true)

            binding.inputCell.doOnTextChanged { text, start, before, count ->
                viewModel.setText(text.toString())
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        coroutineScope.launch {
            viewModel.textFlow.collect {
                with (binding.inputCell) {
                    if (it != text.toString() && it != null) {
                        setText(it)
                        setSelection(it.length)
                    }
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        job.cancel()
    }
}