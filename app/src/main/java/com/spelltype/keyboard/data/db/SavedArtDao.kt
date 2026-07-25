package com.spelltype.keyboard.data.db

import androidx.room.*
import com.spelltype.keyboard.domain.model.SavedArt
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArtDao {
    @Query("SELECT * FROM saved_art ORDER BY timestamp DESC")
    fun getAllSavedArt(): Flow<List<SavedArt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedArt(art: SavedArt)

    @Delete
    suspend fun deleteSavedArt(art: SavedArt)

    @Query("DELETE FROM saved_art")
    suspend fun deleteAll()
}
