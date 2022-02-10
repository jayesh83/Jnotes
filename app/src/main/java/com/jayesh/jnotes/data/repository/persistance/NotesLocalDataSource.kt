package com.jayesh.jnotes.data.repository.persistance

import android.util.Log
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.util.EntityMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "NotesLocalDataSource"

class NotesLocalDataSource @Inject constructor(
    private val mapper: EntityMapper<NoteLocalEntity, Note>
) : INotesLocalDataSource {

    private val notes: ArrayList<NoteLocalEntity> = arrayListOf()

    override suspend fun saveNote(note: Note): DbResult {
        notes.add(mapper.mapFromDomain(note))
        return DbResult.Success
    }

    override suspend fun getNote(id: String): Note? {
        Log.d(TAG, "getNote() called with: id = $id")
        //delay(Random.nextLong(500))
        val noteEntity = notes.find { it.id == id }
        return if (noteEntity != null) {
            mapper.mapToDomain(noteEntity)
        } else {
            null
        }
    }

    override fun getAllNotes(): Flow<List<Note>?> {
        Log.d(TAG, "getAllNotes() called")
        return flow {
            //delay(Random.nextLong(1000))
            val latestNotes = notes
                .map { noteLocalEntity -> mapper.mapToDomain(noteLocalEntity) }
                .sortedByDescending { it.lastEdit }
            emit(latestNotes)
        }
    }

    override suspend fun editNote(id: String, note: Note): DbResult {
        val oldNoteIndexedValue = notes.withIndex().find { it.value.id == id }
        return try {
            if (oldNoteIndexedValue != null) {
                notes[oldNoteIndexedValue.index] = mapper.mapFromDomain(note)
                DbResult.Success
            } else {
                DbResult.Failure
            }
        } catch (e: Exception) {
            DbResult.Failure
        }
    }

    override suspend fun deleteNote(id: String): DbResult {
        val index = notes.indexOfFirst { it.id == id }
        return if (index != -1) {
            notes.removeAt(index)
            DbResult.Success
        } else {
            DbResult.Failure
        }
    }
}