package com.example.lumonote.ui.noteview.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lumonote.R
import com.example.lumonote.databinding.FragmentEditInputBinding
import com.example.lumonote.ui.noteview.viewmodel.InputViewModel
import com.example.lumonote.utils.general.GeneralUIHelper


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

    private var activeTextFormatBtn = false
    private var activeColorBtn = false
    private var activeChecklistBtn = false



    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputViewModel = ViewModelProvider(requireActivity()).get(InputViewModel::class.java)

    }

    // Called when the Fragment creates its view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requireContext() fragment
        _editInputViewBinding = FragmentEditInputBinding.inflate(inflater, container, false)
        return editInputViewBinding.root // return the root view for the fragment
    }

    // Called when the view is created (safe place to interact with UI)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputViewModel.isEditing.observe(viewLifecycleOwner){

            if (inputViewModel.isEditing.value == true) {
                isActive(editInputViewBinding.textFormatButtonIV)
            } else {
                isNotActive(editInputViewBinding.textFormatButtonIV)
            }

            activeTextFormatBtn = inputViewModel.isEditing.value!!

            //Log.d("EditInput", "Point 1")
        }

        editInputViewBinding.apply {

            colorButtonIV.setOnClickListener {
                activeColorBtn = toggleActiveButton(colorButtonIV, activeColorBtn)
            }

            checkListButtonIV.setOnClickListener {
                activeChecklistBtn = toggleActiveButton(checkListButtonIV, activeChecklistBtn)
            }

            imageButtonIV.setOnClickListener {
                isActive(imageButtonIV)
                Handler(Looper.getMainLooper()).postDelayed({
                    // Action here
                    isNotActive(imageButtonIV)
                }, 1000) // 1000 ms = 1 seconds
            }

            textFormatButtonIV.setOnClickListener {
                activeTextFormatBtn = toggleActiveButton(textFormatButtonIV, activeTextFormatBtn)
            }


        }


    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {
        super.onDestroyView()
        _editInputViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun isActive(buttonIV: ImageView) {
        // highlight button
        generalUIHelper.changeButtonIVColor(requireContext(), buttonIV, R.color.gold)
    }

    private fun isNotActive(buttonIV: ImageView) {
        // unhighlight button
        generalUIHelper.changeButtonIVColor(requireContext(), buttonIV, R.color.light_grey_1)

    }

    private fun toggleActiveButton(buttonIV: ImageView, activeFlag: Boolean) : Boolean {

        if (buttonIV == editInputViewBinding.textFormatButtonIV) {
            inputViewModel.setOpenFormatter(!activeFlag)
        }

        return if (!activeFlag) {
            isActive(buttonIV)
            true

        } else {
            isNotActive(buttonIV)
            false
        }
    }


}