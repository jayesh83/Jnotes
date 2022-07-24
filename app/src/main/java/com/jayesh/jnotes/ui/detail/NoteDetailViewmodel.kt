package com.jayesh.jnotes.ui.detail

import androidx.lifecycle.ViewModel
import com.jayesh.jnotes.ui.models.Note

abstract class NoteDetailViewmodel : ViewModel() {
    abstract fun getNoteId(): String?
    abstract fun loadNote(noteId: String?)
    abstract fun saveNote(note: Note)
    abstract fun updateNote(id: String, note: Note)
    abstract fun deleteNote(id: String)
}