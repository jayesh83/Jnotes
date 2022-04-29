package com.jayesh.jnotes.data.repository.persistance

import com.jayesh.jnotes.data.repository.persistance.mapper.NoteMapper
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntityMatchInfo
import com.jayesh.jnotes.ui.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "NotesLocalDataSource"

class NotesDbSourceImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val mapper: NoteMapper
) : NotesDbSource {

    override suspend fun saveNote(note: Note): DbResult {
        notesDao.saveNote(mapper.mapFromDomain(note))
        return DbResult.Success
    }

    override suspend fun getNote(id: String): Note? {
        Timber.e("getNote() called with: id = $id")
        val entity = notesDao.getNote(id)
        return if (entity != null) {
            mapper.mapToDomain(entity)
        } else {
            null
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        Timber.e("getAllNotes() called")
        return notesDao.getAllNotes()
            .map { entitiesList ->
                entitiesList.map { entity ->
                    mapper.mapToDomain(entity)
                }
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun editNote(id: String, note: Note): DbResult {
        Timber.e("editNote() called called with: id= $id")
        notesDao.updateNote(mapper.mapFromDomain(note))
        return DbResult.Success
    }

    override suspend fun deleteNote(id: String): DbResult {
        Timber.e("deleteNote() called called with: id= $id")
        notesDao.deleteNote(id)
        return DbResult.Success
    }

    override suspend fun searchNotes(query: String): List<Note> {
        Timber.e("searchNotes() called called with: query= $query")
        val noteEntitiesMatchInfo: List<NoteLocalEntityMatchInfo> = notesDao.searchNotes(query)
        return noteEntitiesMatchInfo.sortedByDescending {
            OkapiBM25.score(it.matchInfo, column = 0)
        }.map { mapper.mapToDomain(it.noteLocalEntity) }
    }
}