package com.jayesh.jnotes.di

import com.jayesh.jnotes.data.repository.INotesRepository
import com.jayesh.jnotes.data.repository.NotesRepository
import com.jayesh.jnotes.data.repository.persistance.INotesLocalDataSource
import com.jayesh.jnotes.data.repository.persistance.NotesLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Singleton
    @Binds
    fun provideLocalDataSource(
        dataSource: NotesLocalDataSource
    ): INotesLocalDataSource

    @Singleton
    @Binds
    fun provideNotesRepository(
        notesRepository: NotesRepository
    ): INotesRepository
}