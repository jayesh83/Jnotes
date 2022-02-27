package com.jayesh.jnotes.data.repository.persistance.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity.NoteConfigLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity.NoteContentLocalEntity
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.models.Note.NoteConfig
import com.jayesh.jnotes.ui.models.Note.NoteContent
import com.jayesh.jnotes.util.EntityMapper
import javax.inject.Inject

interface NoteMapper : EntityMapper<NoteLocalEntity, Note>

class NoteMapperImpl @Inject constructor() : NoteMapper {
    override fun mapToDomain(entity: NoteLocalEntity): Note {
        return Note(
            title = entity.title,
            content = NoteContent(entity.content.text),
            id = entity.id,
            config = toNoteConfig(entity.config),
            lastEdit = entity.lastEdit,
            createdAt = entity.createdAt
        )
    }

    override fun mapFromDomain(domainModel: Note): NoteLocalEntity {
        return NoteLocalEntity(
            title = domainModel.title,
            content = NoteContentLocalEntity(domainModel.content.text),
            id = domainModel.id,
            config = toNoteConfigLocalEntity(domainModel.config),
            lastEdit = domainModel.lastEdit,
            createdAt = domainModel.createdAt
        )
    }

    private fun toNoteConfig(noteConfig: NoteConfigLocalEntity): NoteConfig {
        return NoteConfig(
            Color(noteConfig.backgroundColor),
            Color(noteConfig.contentColor),
            noteConfig.syncStatus
        )
    }

    private fun toNoteConfigLocalEntity(noteConfig: NoteConfig): NoteConfigLocalEntity {
        return NoteConfigLocalEntity(
            noteConfig.backgroundColor.toArgb(),
            noteConfig.contentColor.toArgb(),
            noteConfig.syncStatus
        )
    }
}