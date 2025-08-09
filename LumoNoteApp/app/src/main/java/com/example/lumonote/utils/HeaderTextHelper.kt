package com.example.lumonote.utils

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.text.getSpans
import com.example.lumonote.databinding.ActivityNoteViewBinding

class HeaderTextHelper {

    private val basicTextHelper: BasicTextHelper = BasicTextHelper()

    fun makeHeader(type: String, viewNoteBinding: ActivityNoteViewBinding) {
        val noteContent = viewNoteBinding.noteContentET
        val stringBuilder = noteContent.text
        val noteContentEnd = stringBuilder?.length ?: 0 // if text is null, set as 0
        val newLine1stIndex =  stringBuilder.toString().indexOf("\n")
        val cursorIndex = noteContent.selectionStart
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

        val absoluteSizeSpans =  stringBuilder?.getSpans<AbsoluteSizeSpan>(selectionStart, selectionEnd)


        Log.d("SelectionStart", "$selectionStart")
        Log.d("SelectionEnd", "$selectionEnd")

        var setSpan: CharacterStyle? = null

        when (type) {
            "normal" -> {
                stringBuilder?.clearSpans()
            }

            "h1" -> {
                setSpan = AbsoluteSizeSpan(24, true)
            }

            "h2" -> {
                setSpan = AbsoluteSizeSpan(21, true)
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

        viewNoteBinding.noteContentET.text = stringBuilder
        viewNoteBinding.noteContentET.setSelection(selectionStart, selectionEnd)


    }






}