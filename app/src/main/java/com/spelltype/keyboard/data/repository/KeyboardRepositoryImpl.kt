package com.spelltype.keyboard.data.repository

import com.spelltype.keyboard.data.db.SavedArtDao
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow

class KeyboardRepositoryImpl(
    private val savedArtDao: SavedArtDao,
    private val preferences: KeyboardPreferences
) : KeyboardRepository {

    override fun getSavedArtList(): Flow<List<SavedArt>> {
        return savedArtDao.getAllSavedArt()
    }

    override suspend fun saveArt(art: SavedArt) {
        savedArtDao.insertSavedArt(art)
    }

    override suspend fun deleteArt(art: SavedArt) {
        savedArtDao.deleteSavedArt(art)
    }

    override fun getSelectedFrameStyle(): Flow<FrameStyle> {
        return preferences.selectedFrameStyleFlow
    }

    override suspend fun saveSelectedFrameStyle(style: FrameStyle) {
        preferences.saveSelectedFrameStyle(style)
    }

    override suspend fun clearAllArt() {
        savedArtDao.deleteAll()
    }
}
