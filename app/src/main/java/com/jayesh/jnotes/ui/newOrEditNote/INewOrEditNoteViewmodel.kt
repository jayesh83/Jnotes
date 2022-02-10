package com.jayesh.jnotes.ui.newOrEditNote

import androidx.lifecycle.ViewModel
import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.models.Note

abstract class INewOrEditNoteViewmodel : ViewModel() {
    abstract suspend fun saveNote(note: Note): DbResult
    abstract suspend fun getNote(id: String): Note?
    abstract suspend fun updateNote(id: String, note: Note): DbResult
    abstract suspend fun deleteNote(id: String): DbResult
}