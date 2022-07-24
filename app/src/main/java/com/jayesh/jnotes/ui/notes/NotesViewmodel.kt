package com.jayesh.jnotes.ui.notes

import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.data.repository.NotesRepo
import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotesViewmodel @Inject constructor(
    private val notesRepository: NotesRepo
) : BaseViewModel<NotesState>(initialState = NotesState()) {
    var searchNotesJob: Job? = null

    init {
        Timber.e("init NotesListingViewmodel")
        observeNotes()
    }

    private fun observeNotes() {
        notesRepository.getAllNotes()
            .onEach { notes ->
                setState { state -> state.copy(isLoading = false, notes = notes, scrollToTop = true) }
                Timber.e("collecting list")
            }
            .onStart { setState { state -> state.copy(isLoading = true) } }
            .launchIn(viewModelScope)
    }

    fun updateScrollToTop(shouldScroll: Boolean) {
        setState { state -> state.copy(scrollToTop = shouldScroll) }
    }

    suspend fun deleteNote(id: String): DbResult {
        return notesRepository.deleteNote(id)
    }

    fun searchNotes(query: String) {
        searchNotesJob?.cancel()
        setState { state -> state.copy(searchQueryText = query.ifBlank { "" }) }

        searchNotesJob = viewModelScope.launch {
            val matchedNotes = if (currentState.searchQueryText.isNotEmpty()) {
                notesRepository.searchNotes(sanitizeSearchQuery(query))
            } else {
                notesRepository.getAllNotes().firstOrNull() ?: emptyList()
            }
            setState { state -> state.copy(notes = matchedNotes) }
        }
    }

    /** FTS(Full text search) query needs to be escaped properly as certain special characters in query, may
     * lead to FTS operations e.g. "-9" search query is treated as (NOT operator 9) **/
    private fun sanitizeSearchQuery(query: String): String {
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }

    override fun onCleared() {
        super.onCleared()
        Timber.e("onCleared NotesViewmodelImpl")
    }
}