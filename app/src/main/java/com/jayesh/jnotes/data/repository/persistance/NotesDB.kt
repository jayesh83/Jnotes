package com.jayesh.jnotes.data.repository.persistance

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jayesh.jnotes.data.repository.persistance.model.NoteLocalEntity

private const val TAG = "NotesDB"

@Database(
    entities = [NoteLocalEntity::class],
    version = 1
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
                            Log.e(TAG, "creating notes database")
                        }
                    }
                ).build()
        }
    }
}