package com.jayesh.jnotes.ui.notesListing

import androidx.lifecycle.ViewModel
import com.jayesh.jnotes.data.repository.persistance.DbResult

abstract class INotesListingViewmodel : ViewModel() {
    abstract suspend fun deleteNote(id: String): DbResult
}