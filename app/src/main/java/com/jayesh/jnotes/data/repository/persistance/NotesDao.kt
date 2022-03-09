package com.jayesh.jnotes.data.repository.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntityMatchInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    /** Create **/
    @Insert(onConflict = REPLACE)
    suspend fun saveNote(note: NoteLocalEntity)

    /** Read **/
    @Query("SELECT * FROM note ORDER BY last_edit DESC")
    fun getAllNotes(): Flow<List<NoteLocalEntity>>

    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun getNote(noteId: String): NoteLocalEntity?

    /** Update **/
    @Update
    suspend fun updateNote(note: NoteLocalEntity): Int

    /** Delete **/
    @Query("DELETE FROM note WHERE id = :noteId")
    suspend fun deleteNote(noteId: String): Int

    @Query(
        """
        SELECT *, matchInfo(note_fts) as matchInfo 
        FROM note
        JOIN note_fts ON note.id = note_fts.id
        WHERE note_fts MATCH :query
        """
    )
    suspend fun searchNotes(query: String): List<NoteLocalEntityMatchInfo>
}