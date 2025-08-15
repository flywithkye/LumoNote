package com.example.lumonote.utils

import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.EditText
import androidx.core.text.getSpans
import com.example.lumonote.data.models.TextSize

class HeaderTextHelper {

    private val basicTextHelper: BasicTextHelper = BasicTextHelper()

    class CustomRelativeSizeSpan(proportion: Float): RelativeSizeSpan(proportion)

    fun makeHeader(type: TextSize, editTextView: EditText) {

        val stringBuilder = editTextView.text
        val noteContentEnd = stringBuilder?.length ?: 0 // if text is null, set as 0
        val newLine1stIndex = stringBuilder.toString().indexOf("\n")
        val cursorIndex = editTextView.selectionStart
        val newLineNextIndex = stringBuilder.toString().indexOf("\n", cursorIndex, false)

        var selectionStart: Int = 0
        var selectionEnd =
            if (newLine1stIndex != -1) {
                // Edit only the current line (up to a "\n")
                newLine1stIndex
            } else {
                // Where no new lines are inserted, jump to end of text
                noteContentEnd
            }

        // if cursor position is after first occurrence of \n
        if (cursorIndex > newLine1stIndex) {
            selectionStart = newLine1stIndex + 1
            //if there exists a newline after the cursor, make that the end, otherwise end of text
            selectionEnd = if (newLineNextIndex != -1) {
                newLineNextIndex
            } else {
                noteContentEnd
            }
        }

        val relativeSizeSpans = stringBuilder?.getSpans<RelativeSizeSpan>(
            selectionStart,
            selectionEnd
        )

        Log.d("relativeSizeSpan", "$relativeSizeSpans")

        if (relativeSizeSpans != null) {
            for (span in relativeSizeSpans) {
                Log.d("Spans", "Span class: ${span::class.java.name}")
            }
        }

        Log.d("SelectionStart", "$selectionStart")
        Log.d("SelectionEnd", "$selectionEnd")

        var setSpan: CharacterStyle? = null

        Log.d("Header", "Point 1")

        when (type) {
            TextSize.NORMAL -> {
                //stringBuilder?.clearSpans()
            }

            TextSize.H1 -> {

                Log.d("Header", "Point 2")

                setSpan = CustomRelativeSizeSpan(1.4f)

                Log.d("Header", "Point 4a")
            }

            TextSize.H2 -> {

                Log.d("Header", "Point 5")

                setSpan = CustomRelativeSizeSpan(1.2f)
            }

            else -> {
                Log.e("ViewNoteActivity", "Type: $type does not exist.")
            }
        }


        stringBuilder?.setSpan(
            setSpan,
            selectionStart,
            selectionEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        basicTextHelper.removeUnintendedUnderlines(stringBuilder)

        editTextView
            .text = stringBuilder
        editTextView.setSelection(selectionStart, selectionEnd)


    }






}