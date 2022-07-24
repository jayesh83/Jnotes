package com.jayesh.jnotes.ui.notes

import com.jayesh.jnotes.ui.State
import com.jayesh.jnotes.ui.models.Note

data class NotesState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val error: String? = null,
    val scrollToTop: Boolean = false,
    val searchQueryText: String = ""
) : State