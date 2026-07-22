package com.spelltype.keyboard

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeKeyboardRepository : KeyboardRepository {

    private val savedArtList = mutableListOf<SavedArt>()
    private val savedArtFlow = MutableStateFlow<List<SavedArt>>(emptyList())
    private val selectedFrameStyleFlow = MutableStateFlow(FrameStyle.NONE)
    private val selectedShapeLayoutFlow = MutableStateFlow(ShapeLayout.NONE)
    private val selectedUnicodeStyleFlow = MutableStateFlow(UnicodeStyle.NONE)
    private val glitterEnabledFlow = MutableStateFlow(false)
    private val customSignatureFlow = MutableStateFlow("")
    private val favoriteStylesFlow = MutableStateFlow<Set<String>>(emptySet())
    private val vibrationEnabledFlow = MutableStateFlow(true)
    private val soundEnabledFlow = MutableStateFlow(true)
    private val themeSelectionFlow = MutableStateFlow("DARK")
    private val premiumUnlockedFlow = MutableStateFlow(false)

    // Phase 6 Flow Mappings
    private val colorfulPreviewEnabledFlow = MutableStateFlow(true)
    private val giantWordsEnabledFlow = MutableStateFlow(false)
    private val keyboardHeightFlow = MutableStateFlow("MEDIUM")
    private val vibrationStrengthFlow = MutableStateFlow(50)
    private val keySoundVolumeFlow = MutableStateFlow(50)
    private val numberRowEnabledFlow = MutableStateFlow(true)
    private val autoSuggestionsEnabledFlow = MutableStateFlow(true)
    private val swipeTypingEnabledFlow = MutableStateFlow(false)

    override fun getSavedArtList(): Flow<List<SavedArt>> {
        return savedArtFlow
    }

    override suspend fun saveArt(art: SavedArt) {
        val newArt = art.copy(id = savedArtList.size + 1)
        savedArtList.add(newArt)
        savedArtFlow.value = savedArtList.toList().reversed()
    }

    override suspend fun deleteArt(art: SavedArt) {
        savedArtList.removeIf { it.id == art.id }
        savedArtFlow.value = savedArtList.toList().reversed()
    }

    override fun getSelectedFrameStyle(): Flow<FrameStyle> {
        return selectedFrameStyleFlow
    }

    override suspend fun saveSelectedFrameStyle(style: FrameStyle) {
        selectedFrameStyleFlow.value = style
    }

    override fun getSelectedShapeLayout(): Flow<ShapeLayout> {
        return selectedShapeLayoutFlow
    }

    override suspend fun saveSelectedShapeLayout(shape: ShapeLayout) {
        selectedShapeLayoutFlow.value = shape
    }

    override fun getSelectedUnicodeStyle(): Flow<UnicodeStyle> {
        return selectedUnicodeStyleFlow
    }

    override suspend fun saveSelectedUnicodeStyle(style: UnicodeStyle) {
        selectedUnicodeStyleFlow.value = style
    }

    override fun getGlitterEnabled(): Flow<Boolean> {
        return glitterEnabledFlow
    }

    override suspend fun saveGlitterEnabled(enabled: Boolean) {
        glitterEnabledFlow.value = enabled
    }

    override fun getCustomSignature(): Flow<String> {
        return customSignatureFlow
    }

    override suspend fun saveCustomSignature(signature: String) {
        customSignatureFlow.value = signature
    }

    override fun getFavoriteStyles(): Flow<Set<String>> {
        return favoriteStylesFlow
    }

    override suspend fun saveFavoriteStyles(favorites: Set<String>) {
        favoriteStylesFlow.value = favorites
    }

    override fun getVibrationEnabled(): Flow<Boolean> {
        return vibrationEnabledFlow
    }

    override suspend fun saveVibrationEnabled(enabled: Boolean) {
        vibrationEnabledFlow.value = enabled
    }

    override fun getSoundEnabled(): Flow<Boolean> {
        return soundEnabledFlow
    }

    override suspend fun saveSoundEnabled(enabled: Boolean) {
        soundEnabledFlow.value = enabled
    }

    override fun getThemeSelection(): Flow<String> {
        return themeSelectionFlow
    }

    override suspend fun saveThemeSelection(theme: String) {
        themeSelectionFlow.value = theme
    }

    override fun getPremiumUnlocked(): Flow<Boolean> {
        return premiumUnlockedFlow
    }

    override suspend fun savePremiumUnlocked(unlocked: Boolean) {
        premiumUnlockedFlow.value = unlocked
    }

    // Phase 6 Flow Implementations
    override fun getColorfulPreviewEnabled(): Flow<Boolean> {
        return colorfulPreviewEnabledFlow
    }

    override suspend fun saveColorfulPreviewEnabled(enabled: Boolean) {
        colorfulPreviewEnabledFlow.value = enabled
    }

    override fun getGiantWordsEnabled(): Flow<Boolean> {
        return giantWordsEnabledFlow
    }

    override suspend fun saveGiantWordsEnabled(enabled: Boolean) {
        giantWordsEnabledFlow.value = enabled
    }

    override fun getKeyboardHeight(): Flow<String> {
        return keyboardHeightFlow
    }

    override suspend fun saveKeyboardHeight(height: String) {
        keyboardHeightFlow.value = height
    }

    override fun getVibrationStrength(): Flow<Int> {
        return vibrationStrengthFlow
    }

    override suspend fun saveVibrationStrength(strength: Int) {
        vibrationStrengthFlow.value = strength
    }

    override fun getKeySoundVolume(): Flow<Int> {
        return keySoundVolumeFlow
    }

    override suspend fun saveKeySoundVolume(volume: Int) {
        keySoundVolumeFlow.value = volume
    }

    override fun getNumberRowEnabled(): Flow<Boolean> {
        return numberRowEnabledFlow
    }

    override suspend fun saveNumberRowEnabled(enabled: Boolean) {
        numberRowEnabledFlow.value = enabled
    }

    override fun getAutoSuggestionsEnabled(): Flow<Boolean> {
        return autoSuggestionsEnabledFlow
    }

    override suspend fun saveAutoSuggestionsEnabled(enabled: Boolean) {
        autoSuggestionsEnabledFlow.value = enabled
    }

    override fun getSwipeTypingEnabled(): Flow<Boolean> {
        return swipeTypingEnabledFlow
    }

    override suspend fun saveSwipeTypingEnabled(enabled: Boolean) {
        swipeTypingEnabledFlow.value = enabled
    }

    override suspend fun clearAllArt() {
        savedArtList.clear()
        savedArtFlow.value = emptyList()
    }
}
