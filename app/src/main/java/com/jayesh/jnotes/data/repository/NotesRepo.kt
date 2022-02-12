package com.jayesh.jnotes.data.repository

import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.ui.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepo {
    /** through local db **/
    suspend fun saveNote(note: Note): DbResult

    suspend fun getNote(id: String): Note?

    fun getAllNotes(): Flow<List<Note>>

    suspend fun editNote(id: String, note: Note): DbResult

    suspend fun deleteNote(id: String): DbResult

    /** through api **/
    fun syncNote(note: Note)

    fun fetchNote(id: String): Flow<Note?>

    fun fetchAllNotes(): Flow<List<Note>?>

    suspend fun editNoteRemote(id: String, note: Note)

    suspend fun deleteNoteRemote(id: String)
}