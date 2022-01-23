package com.jayesh.jnotes.data.repository.persistance.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jayesh.jnotes.data.repository.persistance.model.NoteConfigLocalEntity
import com.jayesh.jnotes.ui.models.NoteConfig
import com.jayesh.jnotes.util.EntityMapper
import javax.inject.Inject

class DbNoteConfigMapper @Inject constructor() :
    EntityMapper<NoteConfigLocalEntity, NoteConfig> {
    override fun mapFromEntity(entity: NoteConfigLocalEntity): NoteConfig {
        return NoteConfig(
            Color(entity.backgroundColor),
            Color(entity.contentColor),
            entity.syncStatus
        )
    }

    override fun mapToEntity(domainModel: NoteConfig): NoteConfigLocalEntity {
        return NoteConfigLocalEntity(
            domainModel.backgroundColor.toArgb(),
            domainModel.contentColor.toArgb(),
            domainModel.syncStatus
        )
    }
}