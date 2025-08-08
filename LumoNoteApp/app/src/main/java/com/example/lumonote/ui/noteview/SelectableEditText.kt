package com.example.lumonote.ui.noteview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class SelectableEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    var onSelectionChange: ((Int, Int) -> Unit)? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChange?.invoke(selStart, selEnd)
    }
}
