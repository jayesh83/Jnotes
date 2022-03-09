package com.jayesh.jnotes.data.repository.persistance.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jayesh.jnotes.ui.models.Note

@Entity(tableName = "note")
data class NoteLocalEntity(
    @ColumnInfo(name = "title")
    val title: String,

    @Embedded
    val content: NoteContentLocalEntity,

    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: String,

    @Embedded
    val config: NoteConfigLocalEntity,

    @ColumnInfo(name = "last_edit")
    val lastEdit: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
) {
    /** Note can contain text, picture, video, drawing, voice recording, etc in future  **/
    data class NoteContentLocalEntity(
        @ColumnInfo(name = "text")
        val text: String
    )

    data class NoteConfigLocalEntity(
        /** argb hex color code **/
        @ColumnInfo(name = "backgroundColor")
        val backgroundColor: Int,

        @ColumnInfo(name = "contentColor")
        val contentColor: Int,

        @ColumnInfo(name = "syncStatus")
        val syncStatus: Note.SyncStatus
    )
}

class Converters {
    // ---- note content
    @TypeConverter
    fun noteContentToString(content: NoteLocalEntity.NoteContentLocalEntity): String {
        return content.text
    }

    @TypeConverter
    fun fromStringToNoteContent(value: String): NoteLocalEntity.NoteContentLocalEntity {
        return NoteLocalEntity.NoteContentLocalEntity(value)
    }

    // ---- note config
    @TypeConverter
    fun noteConfigToString(config: NoteLocalEntity.NoteConfigLocalEntity): String {
        return Gson().toJson(config)
    }

    @TypeConverter
    fun fromNoteConfig(value: String): NoteLocalEntity.NoteConfigLocalEntity {
        return Gson().fromJson(value, NoteLocalEntity.NoteConfigLocalEntity::class.java)
    }
}