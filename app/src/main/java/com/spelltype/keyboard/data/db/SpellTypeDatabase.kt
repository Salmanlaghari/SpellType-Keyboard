package com.spelltype.keyboard.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spelltype.keyboard.domain.model.SavedArt

@Database(entities = [SavedArt::class], version = 1, exportSchema = false)
abstract class SpellTypeDatabase : RoomDatabase() {
    abstract fun savedArtDao(): SavedArtDao

    companion object {
        @Volatile
        private var INSTANCE: SpellTypeDatabase? = null

        fun getDatabase(context: Context): SpellTypeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpellTypeDatabase::class.java,
                    "spelltype_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
