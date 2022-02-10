package com.jayesh.jnotes.data.repository

import com.jayesh.jnotes.data.repository.persistance.DbResult
import com.jayesh.jnotes.data.repository.persistance.INotesLocalDataSource
import com.jayesh.jnotes.ui.models.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "NotesRepository"

class NotesRepository @Inject constructor(
    private val persistence: INotesLocalDataSource
) : INotesRepository {

    override suspend fun saveNote(note: Note): DbResult {
        return persistence.saveNote(note)
    }

    override suspend fun getNote(id: String): Note? {
        return persistence.getNote(id)
    }

    override fun getAllNotes(): Flow<List<Note>?> {
        return persistence.getAllNotes()
    }

    override suspend fun editNote(id: String, note: Note): DbResult {
        return persistence.editNote(id, note)
    }

    override suspend fun deleteNote(id: String): DbResult {
        return persistence.deleteNote(id)
    }

    override fun syncNote(note: Note) {
        TODO("Not yet implemented")
    }

    override fun fetchNote(id: String): Flow<Note?> {
        TODO("Not yet implemented")
    }

    override fun fetchAllNotes(): Flow<List<Note>?> {
        TODO("Not yet implemented")
    }

    override suspend fun editNoteRemote(id: String, note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNoteRemote(id: String) {
        TODO("Not yet implemented")
    }
}