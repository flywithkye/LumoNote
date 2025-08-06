package com.example.lumonote.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lumonote.R
import com.example.lumonote.data.models.Note
import com.example.lumonote.ui.noteview.NoteViewActivity


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class NotesPreviewAdapter(private var notesList: List<Note>, context: Context)
    : RecyclerView.Adapter<NotesPreviewAdapter.NotePreviewViewHolder>()
{
    // The layout from which this view data is accessed is passed into this later
    class NotePreviewViewHolder (notePreviewView: View) : RecyclerView.ViewHolder(notePreviewView) {
        val noteCardPreview: CardView = notePreviewView.findViewById(R.id.notePreviewCV)
        val titlePreview: TextView = notePreviewView.findViewById(R.id.titlePreviewTV)
        val contentPreview: TextView = notePreviewView.findViewById(R.id.contentPreviewTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewViewHolder {
        val notePreviewView = LayoutInflater.from(parent.context).inflate(R.layout.item_note,
            parent, false)

        return NotePreviewViewHolder(notePreviewView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: NotePreviewViewHolder, position: Int) {
        // Find and store the equivalent note object in the list meant to be same as in UI
        val note = notesList[position]

        // Populate the UI note at that position with the data from the note obj at same
        // index in the list
        holder.titlePreview.text = note.noteTitle
        holder.contentPreview.text = note.noteContent

        // Open Note View of note by clicking on one
        holder.noteCardPreview.setOnClickListener {
            val intent = Intent(holder.itemView.context, NoteViewActivity::class.java).apply {
                // Also pass in the id of the note interacted w/ for later retrieval in update note
                putExtra("note_id", note.noteID)
            }

            // Starts the update note activity
            holder.itemView.context.startActivity(intent)
        }
    }

    // Ensure UI stays up-to-date with notes list
    fun refreshData(newNotes: List<Note>) {
        notesList = newNotes

        notifyDataSetChanged()
    }
}