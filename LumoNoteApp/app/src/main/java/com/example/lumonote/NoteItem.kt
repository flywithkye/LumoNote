package com.example.lumonote

// Stores all the data associated with a note element object
data class NoteItem(
    val noteID: Int,

    val noteTitle: String,
    val noteContent: String,

    val noteCreatedDate: String,
    val noteModifiedDate: String
)
