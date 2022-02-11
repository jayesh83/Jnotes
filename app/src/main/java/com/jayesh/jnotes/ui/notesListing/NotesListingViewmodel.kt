package com.jayesh.jnotes.ui.notesListing

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.data.repository.INotesRepository
import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotesListingViewmodel"

@HiltViewModel
class NotesListingViewmodel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: INotesRepository
) : INotesListingViewmodel() {
    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        Log.e(TAG, "init NotesListingViewmodel")
        viewModelScope.launch {
            repository.getAllNotes()
                .collect {
                    _notes.tryEmit(it)
                }
        }
    }

    override suspend fun deleteNote(id: String): DbResult {
        return repository.deleteNote(id)
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "onCleared NotesListingViewmodel")
    }
}