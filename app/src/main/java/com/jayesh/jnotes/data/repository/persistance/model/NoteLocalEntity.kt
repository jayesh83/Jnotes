package com.jayesh.jnotes.data.repository.persistance.model

import com.jayesh.jnotes.ui.models.SyncStatus

data class NoteLocalEntity(
    val title: String,
    val content: NoteContentLocalEntity,
    val id: String,
    val config: NoteConfigLocalEntity,
    val lastEdit: Long,
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