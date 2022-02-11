package com.jayesh.jnotes.data.repository.persistance.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.jayesh.jnotes.ui.models.SyncStatus

@Entity(tableName = "note")
@TypeConverters(Converters::class)
data class NoteLocalEntity(
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: NoteContentLocalEntity,

    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "config")
    val config: NoteConfigLocalEntity,

    @ColumnInfo(name = "last_edit")
    val lastEdit: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)

/** Note can contain text, picture, video, drawing, voice recording, etc  **/
data class NoteContentLocalEntity(
    val text: String
)

data class NoteConfigLocalEntity(
    /** argb hex color code **/
    val backgroundColor: Int,
    val contentColor: Int,
    val syncStatus: SyncStatus
)

class Converters {
    // ---- note content
    @TypeConverter
    fun noteContentToString(content: NoteContentLocalEntity): String {
        return content.text
    }

    @TypeConverter
    fun fromStringToNoteContent(value: String): NoteContentLocalEntity {
        return NoteContentLocalEntity(value)
    }

    // ---- note config
    @TypeConverter
    fun noteConfigToString(config: NoteConfigLocalEntity): String {
        return Gson().toJson(config)
    }

    @TypeConverter
    fun fromNoteConfig(value: String): NoteConfigLocalEntity {
        return Gson().fromJson(value, NoteConfigLocalEntity::class.java)
    }
}