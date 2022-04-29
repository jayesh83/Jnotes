package com.jayesh.jnotes.data.repository.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntityFTS
import timber.log.Timber

private const val TAG = "NotesDB"

@Database(
    entities = [NoteLocalEntity::class, NoteLocalEntityFTS::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDB : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: NotesDB? = null

        fun getInstance(context: Context): NotesDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): NotesDB {
            return Room.databaseBuilder(context, NotesDB::class.java, "notes_db")
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Timber.e("creating notes database")
                        }
                    }
                ).build()
        }
    }
}