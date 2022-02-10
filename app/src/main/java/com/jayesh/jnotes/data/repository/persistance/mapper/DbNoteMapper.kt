package com.jayesh.jnotes.data.repository.persistance.mapper

import com.jayesh.jnotes.data.repository.persistance.model.NoteConfigLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteContentLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.models.NoteConfig
import com.jayesh.jnotes.ui.models.NoteContent
import com.jayesh.jnotes.util.EntityMapper
import javax.inject.Inject

class DbNoteMapper @Inject constructor(
    private val dbNoteConfigMapper: EntityMapper<NoteConfigLocalEntity, NoteConfig>,
    private val dbNoteContentMapper: EntityMapper<NoteContentLocalEntity, NoteContent>,
) : EntityMapper<NoteLocalEntity, Note> {

    override fun mapToDomain(entity: NoteLocalEntity): Note {
        return Note(
            title = entity.title,
            content = dbNoteContentMapper.mapToDomain(entity.content),
            id = entity.id,
            config = dbNoteConfigMapper.mapToDomain(entity.config),
            lastEdit = entity.lastEdit,
            createdAt = entity.createdAt
        )
    }

    override fun mapFromDomain(domainModel: Note): NoteLocalEntity {
        return NoteLocalEntity(
            title = domainModel.title,
            content = dbNoteContentMapper.mapFromDomain(domainModel.content),
            id = domainModel.id,
            config = dbNoteConfigMapper.mapFromDomain(domainModel.config),
            lastEdit = domainModel.lastEdit,
            createdAt = domainModel.createdAt
        )
    }
}