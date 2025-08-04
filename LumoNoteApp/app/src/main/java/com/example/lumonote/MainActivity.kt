package com.example.lumonote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.lumonote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewBinding: ActivityMainBinding
    private lateinit var dbConnection: DatabaseHelper
    private lateinit var notesPreviewAdapter: NotesPreviewAdapter
    private lateinit var tagsDisplayAdapter: TagsDisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainViewBinding.root)


        dbConnection = DatabaseHelper(this)


        notesPreviewAdapter = NotesPreviewAdapter(dbConnection.getAllNotes(), this)

        // Define layout and adapter to use for notes display
        mainViewBinding.notesPreviewRV.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        mainViewBinding.notesPreviewRV.adapter = notesPreviewAdapter


        dbConnection.insertTag(TagItem(1, "All Notes"))
        dbConnection.insertTag(TagItem(2, "School"))
        dbConnection.insertTag(TagItem(3, "Work"))
        dbConnection.insertTag(TagItem(4, "Korean"))
        dbConnection.insertTag(TagItem(5, "Japanese"))
        dbConnection.insertTag(TagItem(6, "Italian"))
        tagsDisplayAdapter = TagsDisplayAdapter(dbConnection.getAllTags(), this)

        // Define layout and adapter to use for tag display
        mainViewBinding.tagsHolderRV.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        mainViewBinding.tagsHolderRV.adapter = tagsDisplayAdapter


        // Log.d("DatePrint", LocalDate.now().toString())

        // Calls reference to the create note floating button in activity_main.xml
        mainViewBinding.createButtonIV.setOnClickListener {
            var intent = Intent(this, ViewNoteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // This ensures that when new notes are added or notes are edited in the database, the
        // notes displayed are up-to-date
        notesPreviewAdapter.refreshData(dbConnection.getAllNotes())

        tagsDisplayAdapter.refreshData(dbConnection.getAllTags())
    }
}