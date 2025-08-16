package com.example.lumonote.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.lumonote.R
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Tag
import com.example.lumonote.databinding.ActivityHomeViewBinding
import com.example.lumonote.ui.noteview.NotePreviewAdapter
import com.example.lumonote.ui.noteview.NoteViewActivity

class HomeViewActivity : AppCompatActivity() {

    private lateinit var homeViewBinding: ActivityHomeViewBinding
    private lateinit var dbConnection: DatabaseHelper
    private lateinit var notePreviewAdapter: NotePreviewAdapter
    private lateinit var tagDisplayAdapter: TagDisplayAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        homeViewBinding = ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(homeViewBinding.root)


        dbConnection = DatabaseHelper(this)


        notePreviewAdapter = NotePreviewAdapter(dbConnection.getAllNotes(), this)

        // Define layout and adapter to use for notes display
        homeViewBinding.notesPreviewRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        homeViewBinding.notesPreviewRV.adapter = notePreviewAdapter


        dbConnection.insertTag(Tag(1, "All Notes"))
        dbConnection.insertTag(Tag(2, "School"))
        dbConnection.insertTag(Tag(3, "Work"))
        dbConnection.insertTag(Tag(4, "Korean"))
        dbConnection.insertTag(Tag(5, "Japanese"))
        dbConnection.insertTag(Tag(6, "Italian"))
        tagDisplayAdapter = TagDisplayAdapter(dbConnection.getAllTags(), this)

        // Define layout and adapter to use for tag display
        homeViewBinding.tagsHolderRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        homeViewBinding.tagsHolderRV.setHasFixedSize(true) // optional but avoids measurement issues
        homeViewBinding.tagsHolderRV.adapter = tagDisplayAdapter


        // Log.d("DatePrint", LocalDate.now().toString())

        // Calls reference to the create note floating button in activity_main.xml
        homeViewBinding.createButtonIV.setOnClickListener {
            var intent = Intent(this, NoteViewActivity::class.java)
            startActivity(intent)
        }

        // Add scroll listener
        homeViewBinding.tagsHolderRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Move the Add button by same scroll distance
                homeViewBinding.tagAddButtonIV.translationX = -recyclerView.computeHorizontalScrollOffset().toFloat()
            }
        })

    }


    override fun onResume() {
        super.onResume()

        // This ensures that when new notes are added or notes are edited in the database, the
        // notes displayed are up-to-date
        notePreviewAdapter.refreshData(dbConnection.getAllNotes())

        tagDisplayAdapter.refreshData(dbConnection.getAllTags())
    }

}