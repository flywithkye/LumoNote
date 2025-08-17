package com.example.lumonote.ui.home.notepreview

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Tag
import com.example.lumonote.databinding.FragmentNotePreviewViewBinding
import com.example.lumonote.ui.home.TagDisplayAdapter
import com.example.lumonote.ui.noteview.NotePreviewAdapter
import com.example.lumonote.ui.noteview.NoteViewActivity

class NotePreviewViewFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_notePrevViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _notePrevViewBinding: FragmentNotePreviewViewBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _notePrevViewBinding!! everywhere
    // The "!!" means it assumes _notePrevViewBinding is not null between onCreateView & onDestroyView
    private val notePrevViewBinding get() = _notePrevViewBinding!!

    private lateinit var dbConnection: DatabaseHelper
    private lateinit var notePreviewAdapter: NotePreviewAdapter
    private lateinit var tagDisplayAdapter: TagDisplayAdapter

    private val inserted = false


    // Called when the Fragment creates its view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _notePrevViewBinding = FragmentNotePreviewViewBinding.inflate(inflater, container, false)
        return notePrevViewBinding.root // return the root view for the fragment
    }

    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbConnection = DatabaseHelper(requireContext())

        dbConnection.insertTag(Tag(1, "All Notes"))
        dbConnection.insertTag(Tag(2, "School"))
        dbConnection.insertTag(Tag(3, "Work"))
        dbConnection.insertTag(Tag(4, "Korean"))
        dbConnection.insertTag(Tag(5, "Japanese"))
        dbConnection.insertTag(Tag(6, "Italian"))

    }

    // Called when the view is created (safe place to interact with UI)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        * In an Activity, you can often use "requireContext()" as the context because
        * an Activity is a subclass of Context.
        * But in a Fragment, requireContext() refers to the Fragment itself, not an Activity or Context.
        * Since Fragment does not inherit from Context, the compiler complains.
        *
        * That’s why you need to explicitly get a Context or Activity from the Fragment:
        * requireContext() → safe way to get the Context (will throw if Fragment isn’t attached).
        */


        notePreviewAdapter = NotePreviewAdapter(dbConnection.getAllNotes(), requireContext())

        // Define layout and adapter to use for notes display
        notePrevViewBinding.notesPreviewRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notePrevViewBinding.notesPreviewRV.adapter = notePreviewAdapter


        tagDisplayAdapter = TagDisplayAdapter(dbConnection.getAllTags(), requireContext())

        // Define layout and adapter to use for tag display
        notePrevViewBinding.tagsHolderRV.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        //notePrevViewBinding.tagsHolderRV.setHasFixedSize(true) // optional but avoids measurement issues
        notePrevViewBinding.tagsHolderRV.adapter = tagDisplayAdapter


        // Log.d("DatePrint", LocalDate.now().toString())

        // Calls reference to the create note floating button
        notePrevViewBinding.createButtonIV.setOnClickListener {
            var intent = Intent(requireContext(), NoteViewActivity::class.java)
            startActivity(intent)
        }

        // Add scroll listener to ensure add tag button scrolls with the tags
        notePrevViewBinding.tagsHolderRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Move the Add button by same scroll distance
                notePrevViewBinding.tagAddButtonIV.translationX =
                    -recyclerView.computeHorizontalScrollOffset().toFloat()
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


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {
        super.onDestroyView()
        _notePrevViewBinding = null // prevent memory leaks by clearing reference
    }
}
