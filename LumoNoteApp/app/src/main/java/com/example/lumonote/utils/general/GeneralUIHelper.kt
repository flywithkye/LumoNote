package com.example.lumonote.utils.general

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GeneralUIHelper {
    fun changeButtonIVColor(context: Context, buttonIV: ImageView, color: Int) {
        buttonIV.imageTintList = ContextCompat.getColorStateList(context, color)
    }

}