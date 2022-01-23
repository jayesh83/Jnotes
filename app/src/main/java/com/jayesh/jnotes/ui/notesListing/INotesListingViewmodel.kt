package com.jayesh.jnotes.ui.notesListing

import androidx.lifecycle.ViewModel
import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.models.Note
import kotlinx.coroutines.flow.Flow

abstract class INotesListingViewmodel : ViewModel() {
    abstract fun getAllNotes(): Flow<List<Note>?>
    abstract suspend fun deleteNote(id: String): DbResult
}