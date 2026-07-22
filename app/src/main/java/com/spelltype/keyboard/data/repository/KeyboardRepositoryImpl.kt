package com.spelltype.keyboard.data.repository

import com.spelltype.keyboard.data.db.SavedArtDao
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
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

    override fun getSelectedShapeLayout(): Flow<ShapeLayout> {
        return preferences.selectedShapeLayoutFlow
    }

    override suspend fun saveSelectedShapeLayout(shape: ShapeLayout) {
        preferences.saveSelectedShapeLayout(shape)
    }

    override fun getSelectedUnicodeStyle(): Flow<UnicodeStyle> {
        return preferences.selectedUnicodeStyleFlow
    }

    override suspend fun saveSelectedUnicodeStyle(style: UnicodeStyle) {
        preferences.saveSelectedUnicodeStyle(style)
    }

    override fun getGlitterEnabled(): Flow<Boolean> {
        return preferences.glitterEnabledFlow
    }

    override suspend fun saveGlitterEnabled(enabled: Boolean) {
        preferences.saveGlitterEnabled(enabled)
    }

    override fun getCustomSignature(): Flow<String> {
        return preferences.customSignatureFlow
    }

    override suspend fun saveCustomSignature(signature: String) {
        preferences.saveCustomSignature(signature)
    }

    override fun getFavoriteStyles(): Flow<Set<String>> {
        return preferences.favoriteStylesFlow
    }

    override suspend fun saveFavoriteStyles(favorites: Set<String>) {
        preferences.saveFavoriteStyles(favorites)
    }

    override fun getVibrationEnabled(): Flow<Boolean> {
        return preferences.vibrationEnabledFlow
    }

    override suspend fun saveVibrationEnabled(enabled: Boolean) {
        preferences.saveVibrationEnabled(enabled)
    }

    override fun getSoundEnabled(): Flow<Boolean> {
        return preferences.soundEnabledFlow
    }

    override suspend fun saveSoundEnabled(enabled: Boolean) {
        preferences.saveSoundEnabled(enabled)
    }

    override fun getThemeSelection(): Flow<String> {
        return preferences.themeSelectionFlow
    }

    override suspend fun saveThemeSelection(theme: String) {
        preferences.saveThemeSelection(theme)
    }

    override fun getPremiumUnlocked(): Flow<Boolean> {
        return preferences.premiumUnlockedFlow
    }

    override suspend fun savePremiumUnlocked(unlocked: Boolean) {
        preferences.savePremiumUnlocked(unlocked)
    }

    override suspend fun clearAllArt() {
        savedArtDao.deleteAll()
    }
}
