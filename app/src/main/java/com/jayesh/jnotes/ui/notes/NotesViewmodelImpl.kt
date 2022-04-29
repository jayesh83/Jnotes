package com.jayesh.jnotes.ui.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.data.repository.NotesRepo
import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "NotesListingViewmodel"

@HiltViewModel
class NotesViewmodelImpl @Inject constructor(
    private val repo: NotesRepo
) : NotesViewmodel() {
    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    val notes = _notes.asStateFlow()

    var searchNotesJob: Job? = null
    val searchQueryText = mutableStateOf("")

    var scrollToTop: Boolean by mutableStateOf(false)
        private set

    init {
        Timber.e("init NotesListingViewmodel")
        viewModelScope.launch {
            repo.getAllNotes()
                .collect { notes ->
                    _notes.value = notes
                    scrollToTop = true  // when there's a new list, scroll list to top
                    Timber.e("collecting list")
                }
        }
    }

    fun updateScrollToTop(shouldScroll: Boolean) {
        scrollToTop = shouldScroll
    }

    override suspend fun deleteNote(id: String): DbResult {
        return repo.deleteNote(id)
    }

    override fun searchNotes(query: String) {
        searchNotesJob?.cancel()
        searchQueryText.value = query.ifBlank { "" }

        searchNotesJob = viewModelScope.launch {
            val matchedNotes = if (searchQueryText.value.isNotEmpty()) {
                repo.searchNotes(sanitizeSearchQuery(query))
            } else {
                repo.getAllNotes().firstOrNull() ?: emptyList()
            }
            _notes.emit(matchedNotes)
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
        Timber.e("onCleared NotesListingViewmodel")
    }
}