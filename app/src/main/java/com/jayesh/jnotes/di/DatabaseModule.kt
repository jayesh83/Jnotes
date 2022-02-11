package com.jayesh.jnotes.di

import android.content.Context
import android.util.Log
import com.jayesh.jnotes.data.repository.persistance.NotesDB
import com.jayesh.jnotes.data.repository.persistance.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TAG = "HiltNoteDatabaseModule"

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideNoteDB(@ApplicationContext context: Context): NotesDB {
        Log.e(TAG, "providing NotesDB")
        return NotesDB.getInstance(context)
    }

    @Provides
    fun provideNotesDao(notesDb: NotesDB): NotesDao {
        Log.e(TAG, "providing NotesDao")
        return notesDb.notesDao()
    }
}