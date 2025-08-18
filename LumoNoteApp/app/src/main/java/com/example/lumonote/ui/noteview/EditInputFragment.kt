package com.example.lumonote.ui.noteview

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.lumonote.R
import com.example.lumonote.data.models.TextSize
import com.example.lumonote.data.models.TextStyle
import com.example.lumonote.databinding.FragmentEditInputBinding
import com.example.lumonote.utils.GeneralUIHelper
import com.example.lumonote.utils.TextSizeHelper
import com.example.lumonote.utils.TextStyleHelper


class EditInputFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_editInputViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _editInputViewBinding: FragmentEditInputBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _editInputViewBinding!! everywhere
    // The "!!" means it assumes _editInputViewBinding is not null between onCreateView & onDestroyView
    private val editInputViewBinding get() = _editInputViewBinding!!

    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var inputViewModel: InputViewModel
    private var textStyleHelper: TextStyleHelper? = null
    private var textSizeHelper: TextSizeHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputViewModel = ViewModelProvider(requireActivity()).get(InputViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requireContext() fragment
        _editInputViewBinding = FragmentEditInputBinding.inflate(inflater, container, false)
        return editInputViewBinding.root // return the root view for the fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textStyleHelper = inputViewModel.textStyleHelper.value
        textSizeHelper = inputViewModel.textSizeHelper.value

        inputViewModel.isEditing.observe(viewLifecycleOwner){
            if (inputViewModel.isEditing.value == true) {
                textFormatterOn()
            } else {
                textFormatterOff()
            }
            //Log.d("EditInput", "Point 1")
        }

        inputViewModel.relativeSizeSpans.observe(viewLifecycleOwner) {

            val relativeSizeSpans = inputViewModel.relativeSizeSpans.value

            if (!relativeSizeSpans.isNullOrEmpty()) {
                for (span in relativeSizeSpans) {
                    Log.d("Relative Spans", "Span class: ${span::class.java.name}")
                }
            } else {
                Log.d("Relative Spans", "None")
            }


            if (!relativeSizeSpans.isNullOrEmpty()) {

                if (relativeSizeSpans[0].sizeChange == TextSize.H1.scaleFactor){
                    generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.h1ButtonIV,
                        R.color.gold)
                    generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.h2ButtonIV,
                        R.color.light_grey_1)
                }
                else if (relativeSizeSpans[0].sizeChange == TextSize.H2.scaleFactor){
                    generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.h2ButtonIV,
                        R.color.gold)
                    generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.h1ButtonIV,
                        R.color.light_grey_1)
                }

                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.normalTextButtonIV,
                    R.color.light_grey_1)
            }
            else {
                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.normalTextButtonIV,
                    R.color.gold)
                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.h1ButtonIV,
                    R.color.light_grey_1)
                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.h2ButtonIV,
                    R.color.light_grey_1)
            }
        }


        inputViewModel.styleSpans.observe(viewLifecycleOwner){
            //Log.d("EditInput", "Point 2")

            val start = inputViewModel.selectionStart.value
            val end = inputViewModel.selectionEnd.value
            val styleSpans = inputViewModel.styleSpans.value ?: arrayOf()


            if (textStyleHelper!!.isAllSpanned(TextStyle.BOLD) ||
                (styleSpans.any { it.style == Typeface.BOLD } && start == end)) {
                //Log.d("FormatBold",  "Point 2")

                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.boldButtonIV,
                    R.color.gold)
            } else {
                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.boldButtonIV,
                    R.color.light_grey_1)
            }

            if (textStyleHelper!!.isAllSpanned(TextStyle.ITALICS) ||
                (styleSpans.any { it.style == Typeface.ITALIC } && start == end)) {
                //Log.d("FormatBold",  "Point 2")

                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.italicsButtonIV,
                    R.color.gold)
            } else {
                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.italicsButtonIV,
                    R.color.light_grey_1)
            }
        }

        inputViewModel.underlineSpans.observe(viewLifecycleOwner) {

            val start = inputViewModel.selectionStart.value
            val end = inputViewModel.selectionEnd.value
            val underlineSpans =
                inputViewModel.underlineSpans.value ?: arrayOf()

            if (textStyleHelper!!.isAllSpanned(TextStyle.UNDERLINE) ||
                (underlineSpans.isNotEmpty() && start == end) ) {

                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.underlineButtonIV,
                    R.color.gold)
            } else {
                generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.underlineButtonIV,
                    R.color.light_grey_1)
            }

        }



        editInputViewBinding.apply {

            textFormatButtonIV.setOnClickListener {
                toggleTextFormatter()
            }

            normalTextButtonIV.setOnClickListener {
                textSizeHelper!!.formatAsHeader(TextSize.NORMAL)
            }
            h1ButtonIV.setOnClickListener {
                textSizeHelper!!.formatAsHeader(TextSize.H1)
            }
            h2ButtonIV.setOnClickListener {
                textSizeHelper!!.formatAsHeader(TextSize.H2)
            }


            boldButtonIV.setOnClickListener {
                textStyleHelper!!.formatText(TextStyle.BOLD)
            }
            italicsButtonIV.setOnClickListener {
                textStyleHelper!!.formatText(TextStyle.ITALICS)
            }
            underlineButtonIV.setOnClickListener {
                textStyleHelper!!.formatText(TextStyle.UNDERLINE)
            }

        }


        editInputViewBinding.clearFormatsButtonIV.setOnClickListener {

            textStyleHelper!!.clearTextStyles()

            generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.boldButtonIV,
                R.color.light_grey_1)
            generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.italicsButtonIV,
                R.color.light_grey_1)
            generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.underlineButtonIV,
                R.color.light_grey_1)
        }
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {
        super.onDestroyView()
        _editInputViewBinding = null // prevent memory leaks by clearing reference
    }

    private fun textFormatterOn() {
        // Show the view
        editInputViewBinding.formatTextSectionRL.visibility = View.VISIBLE
        generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.textFormatButtonIV,
            R.color.gold)
    }

    private fun textFormatterOff() {
        // Hide the view
        editInputViewBinding.formatTextSectionRL.visibility = View.GONE
        generalUIHelper.changeButtonIVColor(requireContext(), editInputViewBinding.textFormatButtonIV,
            R.color.light_grey_1)
    }

    private fun toggleTextFormatter() {
        if (editInputViewBinding.formatTextSectionRL.visibility == View.VISIBLE) {
            textFormatterOff()
        } else {
            textFormatterOn()
        }
    }


}