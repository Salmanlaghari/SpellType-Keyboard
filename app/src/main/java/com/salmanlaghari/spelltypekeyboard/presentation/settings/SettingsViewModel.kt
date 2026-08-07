package com.salmanlaghari.spelltypekeyboard.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.SavedArt
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository
import com.salmanlaghari.spelltypekeyboard.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: KeyboardRepository,
    private val getSavedArtListUseCase: GetSavedArtListUseCase,
    private val deleteArtUseCase: DeleteArtUseCase,
    private val getSelectedFrameStyleUseCase: GetSelectedFrameStyleUseCase,
    private val saveSelectedFrameStyleUseCase: SaveSelectedFrameStyleUseCase
) : ViewModel() {

    private fun <T> Flow<T>.asState(initial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.Eagerly, initial)

    private fun launchSave(block: suspend () -> Unit): Job = viewModelScope.launch { block() }

    val savedArtList: StateFlow<List<SavedArt>> = getSavedArtListUseCase().asState(emptyList())

    val selectedFrameStyle: StateFlow<FrameStyle> = getSelectedFrameStyleUseCase().asState(FrameStyle.NONE)

    val selectedShapeLayout: StateFlow<ShapeLayout> = repository.getSelectedShapeLayout().asState(ShapeLayout.NONE)

    val selectedUnicodeStyle: StateFlow<UnicodeStyle> = repository.getSelectedUnicodeStyle().asState(UnicodeStyle.NONE)

    val glitterEnabled: StateFlow<Boolean> = repository.getGlitterEnabled().asState(false)

    val customSignature: StateFlow<String> = repository.getCustomSignature().asState("")

    val favoriteStyles: StateFlow<Set<String>> = repository.getFavoriteStyles().asState(emptySet())

    val vibrationEnabled: StateFlow<Boolean> = repository.getVibrationEnabled().asState(true)

    val soundEnabled: StateFlow<Boolean> = repository.getSoundEnabled().asState(true)

    val themeSelection: StateFlow<String> = repository.getThemeSelection().asState("DARK")

    val premiumUnlocked: StateFlow<Boolean> = repository.getPremiumUnlocked().asState(false)

    // Phase 6 Flow Exposes
    val colorfulPreviewEnabled: StateFlow<Boolean> = repository.getColorfulPreviewEnabled().asState(true)

    val giantWordsEnabled: StateFlow<Boolean> = repository.getGiantWordsEnabled().asState(false)

    val keyboardHeight: StateFlow<String> = repository.getKeyboardHeight().asState("MEDIUM")

    val vibrationStrength: StateFlow<Int> = repository.getVibrationStrength().asState(50)

    val keySoundVolume: StateFlow<Int> = repository.getKeySoundVolume().asState(50)

    val numberRowEnabled: StateFlow<Boolean> = repository.getNumberRowEnabled().asState(true)

    val autoSuggestionsEnabled: StateFlow<Boolean> = repository.getAutoSuggestionsEnabled().asState(true)

    val swipeTypingEnabled: StateFlow<Boolean> = repository.getSwipeTypingEnabled().asState(false)

    // Premium Configurations StateFlows
    val keyboardWallpaperPath: StateFlow<String> = repository.getKeyboardWallpaperPath().asState("")

    val keyboardWallpaperOpacity: StateFlow<Int> = repository.getKeyboardWallpaperOpacity().asState(50)

    val keyShape: StateFlow<String> = repository.getKeyShape().asState("ROUNDED")

    val keyBorderEnabled: StateFlow<Boolean> = repository.getKeyBorderEnabled().asState(true)

    val keyBorderThickness: StateFlow<Int> = repository.getKeyBorderThickness().asState(1)

    val keyTextSize: StateFlow<String> = repository.getKeyTextSize().asState("MEDIUM")

    fun selectFrameStyle(style: FrameStyle) = launchSave { saveSelectedFrameStyleUseCase(style) }

    fun selectShapeLayout(shape: ShapeLayout) = launchSave { repository.saveSelectedShapeLayout(shape) }

    fun selectUnicodeStyle(style: UnicodeStyle) = launchSave { repository.saveSelectedUnicodeStyle(style) }

    fun setGlitterEnabled(enabled: Boolean) = launchSave { repository.saveGlitterEnabled(enabled) }

    fun setCustomSignature(signature: String) = launchSave { repository.saveCustomSignature(signature) }

    fun toggleFavoriteStyle(style: FrameStyle) = launchSave {
        val current = favoriteStyles.value.toMutableSet()
        if (current.contains(style.name)) {
            current.remove(style.name)
        } else {
            current.add(style.name)
        }
        repository.saveFavoriteStyles(current)
    }

    // Alignment setters matching SettingsActivity.kt exactly
    fun saveVibrationEnabled(enabled: Boolean) = launchSave { repository.saveVibrationEnabled(enabled) }

    fun saveSoundEnabled(enabled: Boolean) = launchSave { repository.saveSoundEnabled(enabled) }

    fun setThemeSelection(theme: String) = launchSave { repository.saveThemeSelection(theme) }

    fun setPremiumUnlocked(unlocked: Boolean) = launchSave { repository.savePremiumUnlocked(unlocked) }

    // Phase 6 Mappings
    fun saveColorfulPreviewEnabled(enabled: Boolean) = launchSave { repository.saveColorfulPreviewEnabled(enabled) }

    fun saveGiantWordsEnabled(enabled: Boolean) = launchSave { repository.saveGiantWordsEnabled(enabled) }

    fun saveKeyboardHeight(height: String) = launchSave { repository.saveKeyboardHeight(height) }

    fun saveVibrationStrength(strength: Int) = launchSave { repository.saveVibrationStrength(strength) }

    fun saveKeySoundVolume(volume: Int) = launchSave { repository.saveKeySoundVolume(volume) }

    fun saveNumberRowEnabled(enabled: Boolean) = launchSave { repository.saveNumberRowEnabled(enabled) }

    fun saveAutoSuggestionsEnabled(enabled: Boolean) = launchSave { repository.saveAutoSuggestionsEnabled(enabled) }

    fun saveSwipeTypingEnabled(enabled: Boolean) = launchSave { repository.saveSwipeTypingEnabled(enabled) }

    fun saveKeyboardWallpaperPath(path: String) = launchSave { repository.saveKeyboardWallpaperPath(path) }

    fun saveKeyboardWallpaperOpacity(opacity: Int) = launchSave { repository.saveKeyboardWallpaperOpacity(opacity) }

    fun saveKeyShape(shape: String) = launchSave { repository.saveKeyShape(shape) }

    fun saveKeyBorderEnabled(enabled: Boolean) = launchSave { repository.saveKeyBorderEnabled(enabled) }

    fun saveKeyBorderThickness(thickness: Int) = launchSave { repository.saveKeyBorderThickness(thickness) }

    fun saveKeyTextSize(size: String) = launchSave { repository.saveKeyTextSize(size) }

    fun deleteArt(art: SavedArt) = launchSave { deleteArtUseCase(art) }

    fun clearAllArt() = launchSave { repository.clearAllArt() }
}

class SettingsViewModelFactory(
    private val repository: KeyboardRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                repository = repository,
                getSavedArtListUseCase = GetSavedArtListUseCase(repository),
                deleteArtUseCase = DeleteArtUseCase(repository),
                getSelectedFrameStyleUseCase = GetSelectedFrameStyleUseCase(repository),
                saveSelectedFrameStyleUseCase = SaveSelectedFrameStyleUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
