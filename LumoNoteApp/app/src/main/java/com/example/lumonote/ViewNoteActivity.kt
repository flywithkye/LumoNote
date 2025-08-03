package com.example.lumonote

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.lumonote.databinding.ActivityViewNoteBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ViewNoteActivity : AppCompatActivity() {

    private lateinit var viewNoteViewBinding: ActivityViewNoteBinding
    private lateinit var dbConnection: DatabaseHelper
    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private lateinit var retrievedNote: NoteItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewNoteViewBinding = ActivityViewNoteBinding.inflate(layoutInflater)
        setContentView(viewNoteViewBinding.root)

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
            val retrievedNoteDate = formatDate(convertedDate)

            // Populate the view note activity UI w/ the pre-existing note data
            viewNoteViewBinding.modifiedDateTV.text = "Edited: $retrievedNoteDate"
            viewNoteViewBinding.noteTitleET.setText(retrievedNote.noteTitle)
            viewNoteViewBinding.noteContentET.setText(retrievedNote.noteContent)

        } else {

            // Display the modified date as current date
            viewNoteViewBinding.modifiedDateTV.text = "Edited: " + formatDate(LocalDate.now())
        }

        viewNoteViewBinding.backButtonIV.setOnClickListener {
            saveNote()

            finish()
        }

        // Calls reference to the button of id deleteButton in note_item.xml
        viewNoteViewBinding.deleteButtonIV.setOnClickListener {

            // Call database helper object and invoke delete note method w/ note id
            dbConnection.deleteNote(retrievedNote.noteID)

            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Note Deleted!", Toast.LENGTH_SHORT).show()
        }

        // Calls reference to the save button of id saveButton in activity_create_note.xml
        viewNoteViewBinding.saveButtonIV.setOnClickListener {
            saveNote()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Custom logic for back button press
                saveNote()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)


    }

    // Submits note to database upon clicking save button
    private fun saveNote() {
        // Collect data from input fields, store in note object
        var title =  viewNoteViewBinding.noteTitleET.text.toString()
        var content =  viewNoteViewBinding.noteContentET.text.toString()

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.now().toString()
        var created = currentDate
        var modified = currentDate

        //Log.d("ViewNote", intent.hasExtra("note_id").toString())

        // If an existing note was clicked on rather than the create button
        if (intent.hasExtra("note_id")) {
            var updatedNote = NoteItem(retrievedNote.noteID, title, content, created, modified)

            // Call database helper object and invoke note update method w/ updated note
            dbConnection.updateNote(updatedNote)

            // Closes view note activity, pops from activity stack, returns to main below it
            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Changes Saved!", Toast.LENGTH_LONG).show()

        } else {
            var newNote = NoteItem(0, title, content, created, modified)

            // Call database helper object and invoke note insertion method w/ new note
            dbConnection.insertNote(newNote)

            // Closes view note activity, pops from activity stack, returns to main below it
            finish()

            // Put small notification popup at bottom of screen
            Toast.makeText(this, "Note Created!", Toast.LENGTH_LONG).show()
        }

    }

    private fun formatDate(date: LocalDate) : String {

        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val currentDate = date.format(formatter)

        Log.d("FormatDate", currentDate)

        // Extract the date from current date, fix it, replace with fixed version

        // Matches numbers 01 to 31 with a trailing comma
        val regex = """\b(0[1-9]|1[0-9]|2[0-9]|3[01]),""".toRegex()

        // Find match in the string
        val currentMonthMatch = regex.find(currentDate)

        // store the numeric part (e.g., "08")
        var monthString = ""

        if (currentMonthMatch != null) {
            // Extract the numeric part (e.g., "08")
            monthString = currentMonthMatch.groupValues[1]
        } else {
            Log.d("FormatDate", "No match found")
        }

        // Convert "08" → 8 → "8,"
        val fixedMonth = "${monthString.toInt()},"

        // Replace original "08," with "8,"
        val fixedCurrentDate = currentDate.replace(regex, fixedMonth)

        Log.d("FormatDate", fixedCurrentDate)


        val weekDay: DayOfWeek = date.dayOfWeek
        val weekDayString = weekDay.toString().lowercase().replaceFirstChar { char ->
            char.titlecaseChar()
        }

        return "$weekDayString, $fixedCurrentDate"

    }
}