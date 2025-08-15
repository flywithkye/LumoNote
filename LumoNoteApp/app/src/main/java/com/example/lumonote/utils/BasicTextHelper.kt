package com.example.lumonote.utils

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText
import androidx.core.text.getSpans
import com.example.lumonote.data.models.TextStyle

class BasicTextHelper (private val editTextView: EditText) {

    private val spanDataMap =  HashMap<Int, Triple<String,Int, Int>>()
    private var spanCounter = 0

    class CustomUnderlineSpan : UnderlineSpan()

    fun isAllSpanned(spanType: TextStyle): Boolean {

        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        val spanCheck = mutableListOf<Boolean>()

        if (selectionStart != selectionEnd) { // Only if text is actually selected

            val selectedText = SpannableString(stringBuilder?.subSequence(selectionStart, selectionEnd))

            for (i in selectedText.indices) {

                val spans = selectedText.getSpans(i, i + 1, CharacterStyle::class.java)

                for (span in spans) {
                    Log.d("Spans", "Span class: ${span::class.java.name}")
                }


                for (span in spans) {
                    if (span is StyleSpan) {
                        val styleName = when (span.style) {
                            Typeface.NORMAL -> "NORMAL"
                            Typeface.BOLD -> TextStyle.BOLD.styleName
                            Typeface.ITALIC -> "ITALIC"
                            Typeface.BOLD_ITALIC -> "BOLD_ITALIC"
                            else -> "UNKNOWN"
                        }
                        Log.d("CharStyle StyleSpan", styleName)
                    }
                    else {
                        Log.d("CharStyle Span", "${span.javaClass.simpleName}")
                    }
                }

                when (spanType) {
                    TextStyle.BOLD, TextStyle.ITALICS -> {
                        var checkType  = if (spanType == TextStyle.BOLD) {
                            Typeface.BOLD
                        } else {
                            Typeface.ITALIC
                        }

                        if (spans.isNotEmpty() && spans.any { it is StyleSpan && it.style == checkType }) {
                            spanCheck.add(true)
                            Log.d("CharStyle", "Char '${selectedText[i]}' at $i has styles: ${spans.joinToString()}")
                        } else {
                            spanCheck.add(false)
                            Log.d("CharStyle","Char '${selectedText[i]}' at $i has no $spanType style")
                        }
                    }

                    TextStyle.UNDERLINE -> {
                        if (spans.isNotEmpty() && spans.any { it is CustomUnderlineSpan } ) {
                            spanCheck.add(true)
                            Log.d("CharStyle","Char '${selectedText[i]}' at $i has underline")
                        } else {
                            spanCheck.add(false)
                            Log.d("CharStyle","Char '${selectedText[i]}' at $i has no underline")
                        }

                    }

                    else -> {
                        Log.e("ViewNoteActivity", "Type: $spanType does not exist.")
                    }
                }

            }
        }

        //Log.d("isAllSpanned", "${spanCheck.all { it }}")

        // check if the selected range has spans
        return spanCheck.all { it }
    }



    fun formatText(type: TextStyle) {

        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        if (selectionStart != selectionEnd) { // Only if text is actually selected

            // hashmap to store past span edits and search for to remove
            // search hashmap to see if selected text contains indices that has style applied to it
            // will use to toggle span display


            var setSpan: CharacterStyle? = null

            when (type) {

                TextStyle.NONE -> {

                    stringBuilder?.clearSpans()
                    spanDataMap.clear()
                }

                TextStyle.BOLD -> setSpan = toggleBasicFormatting(type, Typeface.BOLD)

                TextStyle.ITALICS -> setSpan = toggleBasicFormatting(type, Typeface.ITALIC)

                TextStyle.UNDERLINE -> {

                    val underlineSpans =
                        stringBuilder?.getSpans<CustomUnderlineSpan>(selectionStart, selectionEnd)

                    Log.d("UnderlineSpan", "Point 1")
                    // check if the selected range has underline spans
                    if (isAllSpanned(type)) {
                        // if there are pre-existing words set to underline, remove it
                        if (underlineSpans != null) {
                            for (span in underlineSpans) {
                                stringBuilder?.removeSpan(span)
                            }
                        }
                    } else {
                        // if not, add the underline to everything
                        setSpan = CustomUnderlineSpan()
                    }
                }

            }


            spanDataMap[spanCounter] = Triple(type.styleName, selectionStart, selectionEnd)
            spanCounter++

            stringBuilder?.setSpan(
                setSpan,
                selectionStart,
                selectionEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            removeUnintendedUnderlines(stringBuilder)

            editTextView.text = stringBuilder
            editTextView.setSelection(selectionStart, selectionEnd)

        }
    }



    private fun toggleBasicFormatting(type: TextStyle, typeface: Int)
        : CharacterStyle? {
        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        val styleSpans = stringBuilder?.getSpans<StyleSpan>(selectionStart, selectionEnd)

        // check if all the selected range has the desired style spans
        if (isAllSpanned(type)) {

            // if there are pre-existing words set to that style span, remove it (bold or italics)
            if (styleSpans != null) {
                for (span in styleSpans) {
                    if (span.style == typeface) {
                        stringBuilder?.removeSpan(span)
                    }
                }
            }

            return null
        }
        else {
            // if not, add the text style to everything
            return StyleSpan(typeface)
        }

    }



    fun removeUnintendedUnderlines(editableText: Editable?) {

        if (editableText != null) {
            val allUnderlines = editableText.getSpans(0, editableText.length, UnderlineSpan::class.java)
            for (span in allUnderlines) {
                if (span !is CustomUnderlineSpan) {
                    editableText.removeSpan(span)
                }
            }
        }

    }




}