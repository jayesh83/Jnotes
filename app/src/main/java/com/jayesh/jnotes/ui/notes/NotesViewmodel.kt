package com.jayesh.jnotes.ui.notes

import androidx.lifecycle.ViewModel
import com.jayesh.jnotes.data.repository.persistance.DbResult

abstract class NotesViewmodel : ViewModel() {
    abstract suspend fun deleteNote(id: String): DbResult
}