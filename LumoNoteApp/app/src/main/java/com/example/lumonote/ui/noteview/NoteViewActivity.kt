package com.example.lumonote.ui.noteview

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import com.example.lumonote.R
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Note
import com.example.lumonote.data.models.TextSize
import com.example.lumonote.data.models.TextStyle
import com.example.lumonote.databinding.ActivityNoteViewBinding
import com.example.lumonote.utils.TextStyleHelper
import com.example.lumonote.utils.GeneralTextHelper
import com.example.lumonote.utils.GeneralUIHelper
import com.example.lumonote.utils.TextSizeHelper
import java.time.LocalDate


class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteViewBinding: ActivityNoteViewBinding
    private lateinit var dbConnection: DatabaseHelper
    private lateinit var textStyleHelper: TextStyleHelper
    private lateinit var textSizeHelper: TextSizeHelper
    private val generalTextHelper: GeneralTextHelper = GeneralTextHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private lateinit var retrievedNote: Note


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)

        dbConnection = DatabaseHelper(this)

        textStyleHelper = TextStyleHelper(noteViewBinding.noteContentET)
        textSizeHelper = TextSizeHelper(noteViewBinding.noteContentET)

        noteViewBinding.noteContentET.clearFocusOnKeyboardHide(noteViewBinding.root)
        noteViewBinding.noteTitleET.clearFocusOnKeyboardHide(noteViewBinding.root)

        // If an existing note was clicked on rather than the create button
        if (intent.hasExtra("note_id")) {
            // Retrieve the data passed in from NotesAdapter.kt
            noteID = intent.getIntExtra("note_id", -1)

            // If no note find, exit activity
            if (noteID == -1) {
                finish()
                return
            }

            // Call database helper object and invoke get note method to pull note from database
            retrievedNote = dbConnection.getNoteByID(noteID)

            // Parse the modified date as a date object
            val convertedDate = LocalDate.parse(retrievedNote.noteModifiedDate);
            val retrievedNoteDate = generalTextHelper.formatDate(convertedDate)

            // Populate the view note activity UI w/ the pre-existing note data
            noteViewBinding.modifiedDateTV.text = retrievedNoteDate
            noteViewBinding.noteTitleET.setText(retrievedNote.noteTitle)
            noteViewBinding.noteContentET.setText(retrievedNote.noteContent)

        } else {

            // Display the modified date as current date
            noteViewBinding.modifiedDateTV.text = generalTextHelper.formatDate(LocalDate.now())
        }

        noteViewBinding.backButtonIV.setOnClickListener {
            saveNote()

            finish()
        }

        // Calls reference to the button of id deleteButton in note_item.xml
        noteViewBinding.deleteButtonIV.setOnClickListener {

            // Call database helper object and invoke delete note method w/ note id
            dbConnection.deleteNote(retrievedNote.noteID)

            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show()
        }

        // Calls reference to the button of id pinButton in activity_note_view.xml
        noteViewBinding.pinButtonIV.setOnClickListener {

            val tintColor = noteViewBinding.pinButtonIV.imageTintList
                ?.getColorForState(noteViewBinding.pinButtonIV.drawableState, 0)

            val lightGrey3Tint = ContextCompat.getColor(this, R.color.light_grey_3)

            if (tintColor == lightGrey3Tint) {
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.pinButtonIV,
                    R.color.gold)

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Note Pinned", Toast.LENGTH_SHORT).show()
            } else {
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.pinButtonIV,
                    R.color.light_grey_3)

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Note Unpinned", Toast.LENGTH_SHORT).show()
            }
        }

        // Calls reference to the save button of id saveButton in activity_note_view.xml
        noteViewBinding.saveButtonIV.setOnClickListener {
            saveNote()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Custom logic for back button press
                saveNote()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        noteViewBinding.apply {

            textFormatButtonIV.setOnClickListener {
                toggleTextFormatter()
            }

            normalTextButtonIV.setOnClickListener {
                textSizeHelper.formatAsHeader(TextSize.NORMAL)
            }
            h1ButtonIV.setOnClickListener {
                textSizeHelper.formatAsHeader(TextSize.H1)
            }
            h2ButtonIV.setOnClickListener {
                textSizeHelper.formatAsHeader(TextSize.H2)
            }


            boldButtonIV.setOnClickListener {
                textStyleHelper.formatText(TextStyle.BOLD)
            }
            italicsButtonIV.setOnClickListener {
                textStyleHelper.formatText(TextStyle.ITALICS)
            }
            underlineButtonIV.setOnClickListener {
                textStyleHelper.formatText(TextStyle.UNDERLINE)
            }

        }


        noteViewBinding.clearFormatsButtonIV.setOnClickListener {

            textStyleHelper.clearTextStyles()

            generalUIHelper.changeButtonIVColor(this, noteViewBinding.boldButtonIV,
                R.color.light_grey_1)
            generalUIHelper.changeButtonIVColor(this, noteViewBinding.italicsButtonIV,
                R.color.light_grey_1)
            generalUIHelper.changeButtonIVColor(this, noteViewBinding.underlineButtonIV,
                R.color.light_grey_1)
        }


        // Remove text formatter when text content not being edited
        noteViewBinding.noteContentET.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                textFormatterOff()
            } else {
                textFormatterOn()
            }
        }
        // Highlight each text formatting button
        noteViewBinding.noteContentET.onSelectionChange = { start, end ->

            val noteContent = noteViewBinding.noteContentET
            val selected = noteContent.text?.substring(start, end)
            Log.d("Selection", "Selected: $selected")

            val styleSpans = noteContent.text?.getSpans<StyleSpan>(start, end)
            val underlineSpans =
                noteContent.text?.getSpans<TextStyleHelper.CustomUnderlineSpan>(start, end)

            val relativeSizeSpans = noteContent.text?.getSpans(start,
                end, RelativeSizeSpan::class.java)
            Log.d("relativeSizeSpan", relativeSizeSpans?.contentToString() ?: "null")

            if (!relativeSizeSpans.isNullOrEmpty()) {
                for (span in relativeSizeSpans) {
                    Log.d("Relative Spans", "Span class: ${span::class.java.name}")
                }
            } else {
                Log.d("Relative Spans", "None")
            }


            if (!relativeSizeSpans.isNullOrEmpty()) {

                if (relativeSizeSpans[0].sizeChange == TextSize.H1.scaleFactor){
                    generalUIHelper.changeButtonIVColor(this, noteViewBinding.h1ButtonIV,
                        R.color.gold)
                    generalUIHelper.changeButtonIVColor(this, noteViewBinding.h2ButtonIV,
                        R.color.light_grey_1)
                }
                else if (relativeSizeSpans[0].sizeChange == TextSize.H2.scaleFactor){
                    generalUIHelper.changeButtonIVColor(this, noteViewBinding.h2ButtonIV,
                        R.color.gold)
                    generalUIHelper.changeButtonIVColor(this, noteViewBinding.h1ButtonIV,
                        R.color.light_grey_1)
                }

                generalUIHelper.changeButtonIVColor(this, noteViewBinding.normalTextButtonIV,
                    R.color.light_grey_1)
            }
            else {
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.normalTextButtonIV,
                    R.color.gold)
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.h1ButtonIV,
                    R.color.light_grey_1)
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.h2ButtonIV,
                    R.color.light_grey_1)
            }




            if (textStyleHelper.isAllSpanned(TextStyle.BOLD) ||
                (styleSpans?.any { it.style == Typeface.BOLD } == true && start == end) ) {
                //Log.d("FormatBold",  "Point 2")

                generalUIHelper.changeButtonIVColor(this, noteViewBinding.boldButtonIV,
                    R.color.gold)
            } else {
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.boldButtonIV,
                    R.color.light_grey_1)
            }

            if (textStyleHelper.isAllSpanned(TextStyle.ITALICS) ||
                (styleSpans?.any { it.style == Typeface.ITALIC } == true && start == end) ) {
                //Log.d("FormatBold",  "Point 2")

                generalUIHelper.changeButtonIVColor(this, noteViewBinding.italicsButtonIV,
                    R.color.gold)
            } else {
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.italicsButtonIV,
                    R.color.light_grey_1)
            }

            if (textStyleHelper.isAllSpanned(TextStyle.UNDERLINE) ||
                (underlineSpans?.isNotEmpty() == true && start == end) ) {

                generalUIHelper.changeButtonIVColor(this, noteViewBinding.underlineButtonIV,
                    R.color.gold)
            } else {
                generalUIHelper.changeButtonIVColor(this, noteViewBinding.underlineButtonIV,
                    R.color.light_grey_1)
            }

            //Log.d("FormatBold",  "Point 4")


        }

    }

    private fun textFormatterOn() {
        // Show the view
        noteViewBinding.formatTextSectionRL.visibility = View.VISIBLE
        generalUIHelper.changeButtonIVColor(this, noteViewBinding.textFormatButtonIV, R.color.gold)
    }

    private fun textFormatterOff() {
        // Hide the view
        noteViewBinding.formatTextSectionRL.visibility = View.GONE
        generalUIHelper.changeButtonIVColor(this, noteViewBinding.textFormatButtonIV, R.color.light_grey_1)
    }

    private fun toggleTextFormatter() {
        if (noteViewBinding.formatTextSectionRL.visibility == View.VISIBLE) {
            textFormatterOff()
        } else {
            textFormatterOn()
        }
    }


    // Submits note to database upon clicking save button
    private fun saveNote() {
        // Collect data from input fields, store in note object
        var title =  noteViewBinding.noteTitleET.text.toString()
        var content =  noteViewBinding.noteContentET.text.toString()

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.now().toString()
        var created = currentDate
        var modified = currentDate
        val pinned = false

        //Log.d("ViewNote", intent.hasExtra("note_id").toString())

        // If an existing note was clicked on rather than the create button
        if (intent.hasExtra("note_id")) {
            var updatedNote = Note(retrievedNote.noteID, title, content, created, modified, pinned)

            // Call database helper object and invoke note update method w/ updated note
            dbConnection.updateNote(updatedNote)

            // Closes view note activity, pops from activity stack, returns to main below it
            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_LONG).show()

        } else {
            var newNote = Note(0, title, content, created, modified, pinned)

            // Call database helper object and invoke note insertion method w/ new note
            dbConnection.insertNote(newNote)

            // Closes view note activity, pops from activity stack, returns to main below it
            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Note Created", Toast.LENGTH_LONG).show()
        }

    }

}