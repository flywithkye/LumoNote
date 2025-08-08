package com.example.lumonote.utils

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.core.text.getSpans
import com.example.lumonote.databinding.ActivityNoteViewBinding

class FormatHelperFunctions {

    private val spanDataMap =  HashMap<Int, Triple<String,Int, Int>>()
    private var spanCounter = 0

    class CustomUnderlineSpan : UnderlineSpan()

    fun isAllSpanned(spanType: String, viewNoteBinding: ActivityNoteViewBinding): Boolean {
        val selectionStart: Int = viewNoteBinding.noteContentET.selectionStart
        val selectionEnd: Int = viewNoteBinding.noteContentET.selectionEnd

        val spanCheck = mutableListOf<Boolean>()

        if (selectionStart != selectionEnd) { // Only if text is actually selected
            val stringBuilder = viewNoteBinding.noteContentET.text
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
                            Typeface.BOLD -> "BOLD"
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
                    "bold", "italics" -> {
                        var checkType  = if (spanType == "bold") {
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

                    "underline" -> {
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




    fun formatText(type: String, viewNoteBinding: ActivityNoteViewBinding) {
        val selectionStart: Int = viewNoteBinding.noteContentET.selectionStart
        val selectionEnd: Int = viewNoteBinding.noteContentET.selectionEnd

        if (selectionStart != selectionEnd) { // Only if text is actually selected
            val stringBuilder = viewNoteBinding.noteContentET.text
            val styleSpans =  stringBuilder?.getSpans<StyleSpan>(selectionStart, selectionEnd)
            val underlineSpans =  stringBuilder?.getSpans<CustomUnderlineSpan>(selectionStart, selectionEnd)


            var setSpan: CharacterStyle? = null

            // hashmap to store past span edits and search for to remove
            // search hashmap to see if selected text contains indices that has style applied to it
            // will use to toggle span display

            when (type) {
                "clear" -> {
                    stringBuilder?.clearSpans()
                    spanDataMap.clear()
                }

                "bold" -> {
                    Log.d("FormatBold",  "Point 1")

                    // check if the selected range has bold spans
                    if (isAllSpanned(type, viewNoteBinding)) {
                        Log.d("FormatBold",  "Point 2")
                        // if there are pre-existing words set to bold, remove the bold
                        if (styleSpans != null) {
                            for (span in styleSpans) {
                                if (span.style == Typeface.BOLD) {
                                    stringBuilder.removeSpan(span)
                                }
                            }
                        }
                    } else {
                        Log.d("FormatBold",  "Point 3")
                        // if not, add the bold to everything
                        setSpan = StyleSpan(Typeface.BOLD)

                        spanDataMap[spanCounter] = Triple("BOLD", selectionStart, selectionEnd)
                        spanCounter++
                    }
                    Log.d("FormatBold",  "Point 4")

                }

                "italics" -> {
                    // check if the selected range has bold spans
                    if (isAllSpanned(type, viewNoteBinding)) {
                        // if there are pre-existing words set to bold, remove the bold
                        if (styleSpans != null) {
                            for (span in styleSpans) {
                                if (span.style == Typeface.ITALIC) {
                                    stringBuilder.removeSpan(span)
                                }
                            }
                        }
                    } else {
                        // if not, add the bold to everything
                        setSpan = StyleSpan(Typeface.ITALIC)

                        spanDataMap[spanCounter] = Triple("ITALICS", selectionStart, selectionEnd)
                        spanCounter++
                    }

                }

                "underline" -> {
                    Log.d("UnderlineSpan", "Point 1")
                    // check if the selected range has bold spans
                    if (isAllSpanned(type, viewNoteBinding)) {
                        // if there are pre-existing words set to bold, remove the bold
                        if (underlineSpans != null) {
                            for (span in underlineSpans) {
                                stringBuilder.removeSpan(span)
                            }
                        }
                    } else {
                        // if not, add the bold to everything
                        setSpan = CustomUnderlineSpan()

                        spanDataMap[spanCounter] = Triple("UNDERLINE", selectionStart, selectionEnd)
                        spanCounter++
                    }

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

            removeUnintendedUnderlines(stringBuilder)

            viewNoteBinding.noteContentET.text = stringBuilder
            viewNoteBinding.noteContentET.setSelection(selectionStart, selectionEnd)

        }
    }



    private fun removeUnintendedUnderlines(editableText: Editable?) {
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