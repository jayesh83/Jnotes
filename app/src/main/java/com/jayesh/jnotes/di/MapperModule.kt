package com.jayesh.jnotes.di

import com.jayesh.jnotes.data.repository.persistance.mapper.NoteMapper
import com.jayesh.jnotes.data.repository.persistance.mapper.NoteMapperImpl
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
    fun provideEntityMapper(mapper: NoteMapperImpl): NoteMapper
}