package com.jayesh.jnotes.ui.notes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.data.repository.NotesRepo
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
class NotesViewmodelImpl @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: NotesRepo
) : NotesViewmodel() {
    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        Log.e(TAG, "init NotesListingViewmodel")
        viewModelScope.launch {
            repo.getAllNotes()
                .collect {
                    _notes.tryEmit(it)
                }
        }
    }

    override suspend fun deleteNote(id: String): DbResult {
        return repo.deleteNote(id)
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "onCleared NotesListingViewmodel")
    }
}