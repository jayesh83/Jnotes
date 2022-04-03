package com.jayesh.jnotes.ui.noteDetail

import androidx.lifecycle.ViewModel
import com.jayesh.jnotes.ui.models.Note

abstract class NoteDetailViewmodel : ViewModel() {
    abstract fun loadNote(noteId: String?)
    abstract fun saveNote(note: Note)
    abstract fun updateNote(id: String, note: Note)
    abstract fun deleteNote(id: String)
}