package com.jayesh.jnotes.data.repository.persistance.mapper

import com.jayesh.jnotes.data.repository.persistance.model.NoteContentLocalEntity
import com.jayesh.jnotes.ui.models.NoteContent
import com.jayesh.jnotes.util.EntityMapper
import javax.inject.Inject

class DbNoteContentMapper @Inject constructor() :
    EntityMapper<NoteContentLocalEntity, NoteContent> {
    override fun mapToDomain(entity: NoteContentLocalEntity): NoteContent {
        return NoteContent(entity.text)
    }

    override fun mapFromDomain(domainModel: NoteContent): NoteContentLocalEntity {
        return NoteContentLocalEntity(domainModel.text)
    }
}