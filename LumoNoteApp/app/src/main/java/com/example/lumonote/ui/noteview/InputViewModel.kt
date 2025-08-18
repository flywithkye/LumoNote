package com.example.lumonote.ui.noteview

import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lumonote.utils.TextSizeHelper
import com.example.lumonote.utils.TextStyleHelper

class InputViewModel : ViewModel() {

    private val _textstylehelper = MutableLiveData<TextStyleHelper>()
    val textStyleHelper: LiveData<TextStyleHelper> get() = _textstylehelper
    private val _textsizehelper = MutableLiveData<TextSizeHelper>()
    val textSizeHelper: LiveData<TextSizeHelper> get() = _textsizehelper

    // LiveData to track if EditText is focused
    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> get() = _isEditing


    private val _stylespans = MutableLiveData<Array<out StyleSpan>?>()
    val styleSpans: LiveData<Array<out StyleSpan>?> get() = _stylespans
    private val _underlinespans = MutableLiveData<Array<out TextStyleHelper.CustomUnderlineSpan>?>()
    val underlineSpans: LiveData<Array<out TextStyleHelper.CustomUnderlineSpan>?> get() = _underlinespans
    private val _relativesizespans = MutableLiveData<Array<out RelativeSizeSpan>?>()
    val relativeSizeSpans: LiveData<Array<out RelativeSizeSpan>?> get() = _relativesizespans

    private val _selectionstart = MutableLiveData<Int>()
    val selectionStart: LiveData<Int> get() = _selectionstart
    private val _selectionend = MutableLiveData<Int>()
    val selectionEnd: LiveData<Int> get() = _selectionend



    fun setTextStyleHelper(textStyleHelper: TextStyleHelper) {
        _textstylehelper.value = textStyleHelper
    }

    fun setTextSizeHelper(textSizeHelper: TextSizeHelper) {
        _textsizehelper.value = textSizeHelper
    }

    // Call this when EditText gains/loses focus
    fun setEditing(focused: Boolean) {
        _isEditing.value = focused
    }

    fun setStyleSpans(styleSpans: Array<out StyleSpan>?) {
        _stylespans.value = styleSpans
    }
    fun setUnderlineSpans(underlineSpans: Array<out TextStyleHelper.CustomUnderlineSpan>?) {
        _underlinespans.value = underlineSpans
    }
    fun setRelativeSizeSpans(relativeSizeSpans: Array<out RelativeSizeSpan>?) {
        _relativesizespans.value = relativeSizeSpans
    }

    fun setSelectionStart(selectionStart: Int) {
        _selectionstart.value = selectionStart
    }
    fun setSelectionEnd(selectionEnd: Int) {
        _selectionend.value = selectionEnd
    }




}