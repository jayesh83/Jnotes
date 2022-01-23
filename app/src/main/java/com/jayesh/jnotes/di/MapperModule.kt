package com.jayesh.jnotes.di

import com.jayesh.jnotes.data.repository.persistance.mapper.DbNoteConfigMapper
import com.jayesh.jnotes.data.repository.persistance.mapper.DbNoteContentMapper
import com.jayesh.jnotes.data.repository.persistance.mapper.DbNoteMapper
import com.jayesh.jnotes.data.repository.persistance.model.NoteConfigLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteContentLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.models.NoteConfig
import com.jayesh.jnotes.ui.models.NoteContent
import com.jayesh.jnotes.util.EntityMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MapperModule {
    @Singleton
    @Binds
    fun provideNoteMapper(
        dbNoteMapper: DbNoteMapper
    ): EntityMapper<NoteLocalEntity, Note>

    @Singleton
    @Binds
    fun provideNoteContentMapper(
        dbNoteContentMapper: DbNoteContentMapper
    ): EntityMapper<NoteContentLocalEntity, NoteContent>

    @Singleton
    @Binds
    fun provideNoteConfigMapper(
        dbNoteConfigMapper: DbNoteConfigMapper
    ): EntityMapper<NoteConfigLocalEntity, NoteConfig>
}