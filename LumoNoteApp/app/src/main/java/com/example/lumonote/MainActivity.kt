package com.example.lumonote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Tag
import com.example.lumonote.databinding.ActivityMainBinding
import com.example.lumonote.ui.noteview.NoteViewActivity
import com.example.lumonote.utils.NotePreviewAdapter
import com.example.lumonote.utils.TagDisplayAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewBinding: ActivityMainBinding
    private lateinit var dbConnection: DatabaseHelper
    private lateinit var notePreviewAdapter: NotePreviewAdapter
    private lateinit var tagDisplayAdapter: TagDisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainViewBinding.root)


        dbConnection = DatabaseHelper(this)


        notePreviewAdapter = NotePreviewAdapter(dbConnection.getAllNotes(), this)

        // Define layout and adapter to use for notes display
        mainViewBinding.notesPreviewRV.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        mainViewBinding.notesPreviewRV.adapter = notePreviewAdapter


        dbConnection.insertTag(Tag(1, "All Notes"))
        dbConnection.insertTag(Tag(2, "School"))
        dbConnection.insertTag(Tag(3, "Work"))
        dbConnection.insertTag(Tag(4, "Korean"))
        dbConnection.insertTag(Tag(5, "Japanese"))
        dbConnection.insertTag(Tag(6, "Italian"))
        tagDisplayAdapter = TagDisplayAdapter(dbConnection.getAllTags(), this)

        // Define layout and adapter to use for tag display
        mainViewBinding.tagsHolderRV.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        mainViewBinding.tagsHolderRV.adapter = tagDisplayAdapter


        // Log.d("DatePrint", LocalDate.now().toString())

        // Calls reference to the create note floating button in activity_main.xml
        mainViewBinding.createButtonIV.setOnClickListener {
            var intent = Intent(this, NoteViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // This ensures that when new notes are added or notes are edited in the database, the
        // notes displayed are up-to-date
        notePreviewAdapter.refreshData(dbConnection.getAllNotes())

        tagDisplayAdapter.refreshData(dbConnection.getAllTags())
    }
}