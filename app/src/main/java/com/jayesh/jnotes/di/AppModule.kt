package com.jayesh.jnotes.di

import com.jayesh.jnotes.data.repository.NotesRepo
import com.jayesh.jnotes.data.repository.NotesRepoImpl
import com.jayesh.jnotes.data.repository.persistance.NotesDbSource
import com.jayesh.jnotes.data.repository.persistance.NotesDbSourceImpl
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
    fun provideDbSource(
        dbSourceImpl: NotesDbSourceImpl
    ): NotesDbSource

    @Singleton
    @Binds
    fun provideNotesRepository(
        notesRepoImpl: NotesRepoImpl
    ): NotesRepo
}