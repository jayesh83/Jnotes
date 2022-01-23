package com.jayesh.jnotes.ui.notesListing

import androidx.lifecycle.SavedStateHandle
import com.jayesh.jnotes.data.repository.INotesRepository
import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "NotesListingViewmodel"

@HiltViewModel
class NotesListingViewmodel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val INotesRepository: INotesRepository
) : INotesListingViewmodel() {

    override fun getAllNotes(): Flow<List<Note>?> {
        return INotesRepository.getAllNotes()
    }

    override suspend fun deleteNote(id: String): DbResult {
        return INotesRepository.deleteNote(id)
    }
}