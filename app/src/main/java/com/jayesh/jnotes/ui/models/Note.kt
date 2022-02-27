package com.jayesh.jnotes.ui.models

import androidx.compose.ui.graphics.Color
import com.jayesh.jnotes.ui.theme.BlackMuted
import com.jayesh.jnotes.ui.theme.WhiteMutated
import java.util.UUID

data class Note(
    val title: String,
    val content: NoteContent = NoteContent.DEFAULT,
    val id: String = UUID.randomUUID()?.toString() ?: System.currentTimeMillis().toString(),
    val config: NoteConfig = NoteConfig.DEFAULT,
    val lastEdit: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val isTitleTextEmpty get() = title.isBlank()
    val isContentTextEmpty get() = content.text.isBlank()

    /** Note can contain text, picture, video, drawing, voice recording, etc  **/
    data class NoteContent(
        val text: String
    ) {
        companion object {
            val DEFAULT = NoteContent(text = "")
        }
    }

    data class NoteConfig(
        /** argb hex color code **/
        val backgroundColor: Color,
        val contentColor: Color,
        val syncStatus: SyncStatus
    ) {
        val isSynced get() = syncStatus == SyncStatus.SYNCED

        companion object {
            val DEFAULT = NoteConfig(
                backgroundColor = WhiteMutated,
                contentColor = BlackMuted,
                syncStatus = SyncStatus.NOT_SYNCED
            )
        }
    }

    enum class SyncStatus(val status: Int) {
        NOT_SYNCED(-1),
        SYNC_FAILED(0),
        SYNCED(1);

        companion object {
            fun fromInt(status: Int): SyncStatus? {
                return values().find { it.status == status }
            }
        }
    }
}