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

    // Phase 6 Mappings
    override fun getColorfulPreviewEnabled(): Flow<Boolean> {
        return preferences.colorfulPreviewEnabledFlow
    }

    override suspend fun saveColorfulPreviewEnabled(enabled: Boolean) {
        preferences.saveColorfulPreviewEnabled(enabled)
    }

    override fun getGiantWordsEnabled(): Flow<Boolean> {
        return preferences.giantWordsEnabledFlow
    }

    override suspend fun saveGiantWordsEnabled(enabled: Boolean) {
        preferences.saveGiantWordsEnabled(enabled)
    }

    override fun getKeyboardHeight(): Flow<String> {
        return preferences.keyboardHeightFlow
    }

    override suspend fun saveKeyboardHeight(height: String) {
        preferences.saveKeyboardHeight(height)
    }

    override fun getVibrationStrength(): Flow<Int> {
        return preferences.vibrationStrengthFlow
    }

    override suspend fun saveVibrationStrength(strength: Int) {
        preferences.saveVibrationStrength(strength)
    }

    override fun getKeySoundVolume(): Flow<Int> {
        return preferences.keySoundVolumeFlow
    }

    override suspend fun saveKeySoundVolume(volume: Int) {
        preferences.saveKeySoundVolume(volume)
    }

    override fun getNumberRowEnabled(): Flow<Boolean> {
        return preferences.numberRowEnabledFlow
    }

    override suspend fun saveNumberRowEnabled(enabled: Boolean) {
        preferences.saveNumberRowEnabled(enabled)
    }

    override fun getAutoSuggestionsEnabled(): Flow<Boolean> {
        return preferences.autoSuggestionsEnabledFlow
    }

    override suspend fun saveAutoSuggestionsEnabled(enabled: Boolean) {
        preferences.saveAutoSuggestionsEnabled(enabled)
    }

    override fun getSwipeTypingEnabled(): Flow<Boolean> {
        return preferences.swipeTypingEnabledFlow
    }

    override suspend fun saveSwipeTypingEnabled(enabled: Boolean) {
        preferences.saveSwipeTypingEnabled(enabled)
    }

    override suspend fun clearAllArt() {
        savedArtDao.deleteAll()
    }
}
