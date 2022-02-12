package com.jayesh.jnotes.data.repository.persistance

import com.jayesh.jnotes.ui.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesDbSource {
    suspend fun saveNote(note: Note): DbResult
    suspend fun getNote(id: String): Note?
    fun getAllNotes(): Flow<List<Note>>
    suspend fun editNote(id: String, note: Note): DbResult
    suspend fun deleteNote(id: String): DbResult
}

enum class DbResult {
    Failure,
    Loading,
    Success
}