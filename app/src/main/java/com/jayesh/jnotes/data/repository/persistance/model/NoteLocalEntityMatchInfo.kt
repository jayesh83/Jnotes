package com.jayesh.jnotes.data.repository.persistance.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class NoteLocalEntityMatchInfo(
    @Embedded
    val noteLocalEntity: NoteLocalEntity,
    @ColumnInfo(name = "matchInfo")
    val matchInfo: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteLocalEntityMatchInfo

        if (noteLocalEntity != other.noteLocalEntity) return false
        if (!matchInfo.contentEquals(other.matchInfo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = noteLocalEntity.hashCode()
        result = 31 * result + matchInfo.contentHashCode()
        return result
    }
}

