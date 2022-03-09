package com.jayesh.jnotes.data.repository.persistance.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "note_fts")
@Fts4(contentEntity = NoteLocalEntity::class)
data class NoteLocalEntityFTS(
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "text")
    val textContent: String,

    @ColumnInfo(name = "last_edit")
    val lastEdit: String
)
