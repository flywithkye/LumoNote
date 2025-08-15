package com.example.lumonote.utils

import android.text.Editable
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.EditText
import com.example.lumonote.data.models.TextSize

class HeaderTextHelper(private val editTextView: EditText) {

    private val basicTextHelper: BasicTextHelper = BasicTextHelper(editTextView)

    class CustomRelativeSizeSpan(proportion: Float): RelativeSizeSpan(proportion)

    fun formatAsHeader(type: TextSize) {

        val stringBuilder: Editable? = editTextView.text
        var setSpan: CharacterStyle? = null

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

        Log.d("SelectionStart", "$selectionStart")
        Log.d("SelectionEnd", "$selectionEnd")


        val relativeSizeSpans = editTextView.text?.getSpans(selectionStart,
            selectionEnd, RelativeSizeSpan::class.java)


        Log.d("Header", "Point 1")

        when (type) {
            TextSize.NORMAL -> {
                // check if heading size has already been applied
                if (!relativeSizeSpans.isNullOrEmpty()) {
                    // if so, remove it
                    for (span in relativeSizeSpans) {
                        stringBuilder?.removeSpan(span)
                    }
                }
            }

            TextSize.H1 -> setSpan = toggleHeader(relativeSizeSpans, TextSize.H1.scaleFactor)

            TextSize.H2 -> setSpan = toggleHeader(relativeSizeSpans, TextSize.H2.scaleFactor)
        }


        stringBuilder?.setSpan(
            setSpan,
            selectionStart,
            selectionEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        basicTextHelper.removeUnintendedUnderlines(stringBuilder)

        editTextView.text = stringBuilder
        editTextView.setSelection(selectionStart, selectionEnd)


    }



    private fun toggleHeader(spanList: Array<RelativeSizeSpan>?, scaleFactor: Float)
        : CharacterStyle? {

        val stringBuilder: Editable? = editTextView.text

        Log.d("relativeSizeSpan", spanList.contentToString())

        if (!spanList.isNullOrEmpty()) {
            for (span in spanList) {
                Log.d("Relative Spans", "Span class: ${span::class.java.name}")
            }
        } else {
            Log.d("Relative Spans", "None")
        }



        // check if heading size has already been applied
        if (spanList.isNullOrEmpty()) {
            // If not, add it
            return RelativeSizeSpan(scaleFactor)
        }
        else if (spanList.isNotEmpty()) {
            // If different header was applied, replace it
            for (span in spanList) {
                stringBuilder?.removeSpan(span)
            }
            return RelativeSizeSpan(scaleFactor)
        }
        else {
            // if so, remove it (toggle)
            for (span in spanList) {
                stringBuilder?.removeSpan(span)
            }
            return null
        }

    }


}