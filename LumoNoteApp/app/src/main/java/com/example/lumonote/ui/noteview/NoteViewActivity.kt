package com.example.lumonote.ui.noteview

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lumonote.R
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Note
import com.example.lumonote.data.models.TextSize
import com.example.lumonote.data.models.TextStyle
import com.example.lumonote.databinding.ActivityNoteViewBinding
import com.example.lumonote.utils.BasicTextHelper
import com.example.lumonote.utils.GeneralHelperFunctions
import com.example.lumonote.utils.HeaderTextHelper
import java.time.LocalDate


class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteViewBinding: ActivityNoteViewBinding
    private lateinit var dbConnection: DatabaseHelper
    private val basicTextHelper: BasicTextHelper = BasicTextHelper()
    private val headerTextHelper: HeaderTextHelper = HeaderTextHelper()
    private val generalHelper: GeneralHelperFunctions = GeneralHelperFunctions()

    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private lateinit var retrievedNote: Note


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)

        dbConnection = DatabaseHelper(this)

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
            val retrievedNoteDate = generalHelper.formatDate(convertedDate)

            // Populate the view note activity UI w/ the pre-existing note data
            noteViewBinding.modifiedDateTV.text = "Edited: $retrievedNoteDate"
            noteViewBinding.noteTitleET.setText(retrievedNote.noteTitle)
            noteViewBinding.noteContentET.setText(retrievedNote.noteContent)

        } else {

            // Display the modified date as current date
            noteViewBinding.modifiedDateTV.text = "Edited: " + generalHelper.formatDate(LocalDate.now())
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
            Toast.makeText(this, "Note Deleted!", Toast.LENGTH_SHORT).show()
        }

        // Calls reference to the save button of id saveButton in activity_create_note.xml
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

            clearFormatsButtonIV.setOnClickListener {
                basicTextHelper.formatText(TextStyle.NONE, noteViewBinding.noteContentET)
            }

            h1ButtonIV.setOnClickListener {
                headerTextHelper.makeHeader(TextSize.H1, noteViewBinding.noteContentET)
            }
            h2ButtonIV.setOnClickListener {
                headerTextHelper.makeHeader(TextSize.H2, noteViewBinding.noteContentET)
            }

            boldButtonIV.setOnClickListener {
                basicTextHelper.formatText(TextStyle.BOLD,  noteViewBinding.noteContentET)
            }
            italicsButtonIV.setOnClickListener {
                basicTextHelper.formatText(TextStyle.ITALICS, noteViewBinding.noteContentET)
            }
            underlineButtonIV.setOnClickListener {
                basicTextHelper.formatText(TextStyle.UNDERLINE, noteViewBinding.noteContentET)
            }

        }

        // Highlight each text formatting button
        noteViewBinding.noteContentET.onSelectionChange = { start, end ->
            if (start != end) {
                noteViewBinding.formatTextSectionCL.visibility = View.VISIBLE // Show the view

                val selected = noteViewBinding.noteContentET.text?.substring(start, end)
                Log.d("Selection", "Selected: $selected")


                if (basicTextHelper.isAllSpanned(TextStyle.BOLD, noteViewBinding.noteContentET)) {
                    //Log.d("FormatBold",  "Point 2")

                    changeButtonIVColor(noteViewBinding.boldButtonIV, R.color.gold)
                }

                if (basicTextHelper.isAllSpanned(TextStyle.ITALICS, noteViewBinding.noteContentET)) {
                    //Log.d("FormatBold",  "Point 2")

                    changeButtonIVColor(noteViewBinding.italicsButtonIV, R.color.gold)
                }

                if (basicTextHelper.isAllSpanned(TextStyle.UNDERLINE, noteViewBinding.noteContentET)) {

                    changeButtonIVColor(noteViewBinding.underlineButtonIV, R.color.gold)
                }

                //Log.d("FormatBold",  "Point 4")
            }

            else {
                // Ensure buttons are grey by default
                changeButtonIVColor(noteViewBinding.boldButtonIV, R.color.light_grey_1)
                changeButtonIVColor(noteViewBinding.italicsButtonIV, R.color.light_grey_1)
                changeButtonIVColor(noteViewBinding.underlineButtonIV, R.color.light_grey_1)
            }
        }

    }


    private fun toggleTextFormatter() {
        if (noteViewBinding.formatTextSectionCL.visibility == View.VISIBLE) {
            noteViewBinding.formatTextSectionCL.visibility = View.GONE // Hide the view
        } else {
            noteViewBinding.formatTextSectionCL.visibility = View.VISIBLE // Show the view
        }
    }

    private fun changeButtonIVColor(buttonIV: ImageView, color: Int) {
        buttonIV.imageTintList = ContextCompat.getColorStateList(this, color)
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

        //Log.d("ViewNote", intent.hasExtra("note_id").toString())

        // If an existing note was clicked on rather than the create button
        if (intent.hasExtra("note_id")) {
            var updatedNote = Note(retrievedNote.noteID, title, content, created, modified)

            // Call database helper object and invoke note update method w/ updated note
            dbConnection.updateNote(updatedNote)

            // Closes view note activity, pops from activity stack, returns to main below it
            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Changes Saved!", Toast.LENGTH_LONG).show()

        } else {
            var newNote = Note(0, title, content, created, modified)

            // Call database helper object and invoke note insertion method w/ new note
            dbConnection.insertNote(newNote)

            // Closes view note activity, pops from activity stack, returns to main below it
            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Note Created!", Toast.LENGTH_LONG).show()
        }

    }

}