package com.jayesh.jnotes.data.repository.persistance

import android.util.Log
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.util.EntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "NotesLocalDataSource"

class NotesLocalDataSource @Inject constructor(
    private val notesDao: NotesDao,
    private val mapper: EntityMapper<NoteLocalEntity, Note>
) : INotesLocalDataSource {

    override suspend fun saveNote(note: Note): DbResult {
        notesDao.saveNote(mapper.mapFromDomain(note))
        return DbResult.Success
    }

    override suspend fun getNote(id: String): Note? {
        Log.e(TAG, "getNote() called with: id = $id")
        val entity = notesDao.getNote(id)
        return if (entity != null) {
            mapper.mapToDomain(entity)
        } else {
            null
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        Log.e(TAG, "getAllNotes() called")
        return notesDao.getAllNotes()
            .map { entitiesList ->
                Log.d(TAG, "getAllNotes() notesDao.getAllNotes().map")
                entitiesList
                    .map { entity ->
                    Log.d(TAG, "getAllNotes() entitiesList.map")
                    mapper.mapToDomain(entity)
                }
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun editNote(id: String, note: Note): DbResult {
        notesDao.updateNote(mapper.mapFromDomain(note))
        return DbResult.Success
    }

    override suspend fun deleteNote(id: String): DbResult {
        notesDao.deleteNote(id)
        return DbResult.Success
    }
}