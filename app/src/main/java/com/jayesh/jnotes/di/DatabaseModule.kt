package com.jayesh.jnotes.di

import android.content.Context
import com.jayesh.jnotes.data.repository.persistance.NotesDB
import com.jayesh.jnotes.data.repository.persistance.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

private const val TAG = "HiltNoteDatabaseModule"

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideNoteDB(@ApplicationContext context: Context): NotesDB {
        Timber.e("providing NotesDB")
        return NotesDB.getInstance(context)
    }

    @Provides
    fun provideNotesDao(notesDb: NotesDB): NotesDao {
        Timber.e("providing NotesDao")
        return notesDb.notesDao()
    }
}