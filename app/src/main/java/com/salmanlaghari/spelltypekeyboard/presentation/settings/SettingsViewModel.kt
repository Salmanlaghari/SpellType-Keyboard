package com.salmanlaghari.spelltypekeyboard.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.salmanlaghari.spelltypekeyboard.core.AppLog
import com.salmanlaghari.spelltypekeyboard.core.rethrowIfCancellation
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.SavedArt
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository
import com.salmanlaghari.spelltypekeyboard.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
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

    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /** Messages for writes that failed, so the UI can tell the user instead of silently ignoring it. */
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    /**
     * Runs a persistence call, reporting failures on [errors] rather than letting them escape the
     * scope (which crashes the process) or disappear unnoticed.
     */
    private fun launchSaving(operation: String, description: String, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                e.rethrowIfCancellation()
                AppLog.e("SettingsViewModel.$operation", e)
                _errors.tryEmit("Couldn't save $description")
            }
        }
    }

    val savedArtList: StateFlow<List<SavedArt>> = getSavedArtListUseCase()
        .catch { error ->
            AppLog.e("SettingsViewModel.savedArtList", error)
            _errors.tryEmit("Couldn't load your saved art")
            emit(emptyList())
        }.asState(emptyList())

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

    fun selectFrameStyle(style: FrameStyle) = launchSaving("selectFrameStyle", "frame style") { saveSelectedFrameStyleUseCase(style) }

    fun selectShapeLayout(shape: ShapeLayout) = launchSaving("selectShapeLayout", "shape layout") { repository.saveSelectedShapeLayout(shape) }

    fun selectUnicodeStyle(style: UnicodeStyle) = launchSaving("selectUnicodeStyle", "text style") { repository.saveSelectedUnicodeStyle(style) }

    fun setGlitterEnabled(enabled: Boolean) = launchSaving("setGlitterEnabled", "glitter setting") { repository.saveGlitterEnabled(enabled) }

    fun setCustomSignature(signature: String) = launchSaving("setCustomSignature", "signature") { repository.saveCustomSignature(signature) }

    fun toggleFavoriteStyle(style: FrameStyle) = launchSaving("toggleFavoriteStyle", "favourites") {
        val current = favoriteStyles.value.toMutableSet()
        if (current.contains(style.name)) {
            current.remove(style.name)
        } else {
            current.add(style.name)
        }
        repository.saveFavoriteStyles(current)
    }

    // Alignment setters matching SettingsActivity.kt exactly
    fun saveVibrationEnabled(enabled: Boolean) = launchSaving("saveVibrationEnabled", "vibration setting") { repository.saveVibrationEnabled(enabled) }

    fun saveSoundEnabled(enabled: Boolean) = launchSaving("saveSoundEnabled", "sound setting") { repository.saveSoundEnabled(enabled) }

    fun setThemeSelection(theme: String) = launchSaving("setThemeSelection", "theme") { repository.saveThemeSelection(theme) }

    fun setPremiumUnlocked(unlocked: Boolean) = launchSaving("setPremiumUnlocked", "premium status") { repository.savePremiumUnlocked(unlocked) }

    // Phase 6 Mappings
    fun saveColorfulPreviewEnabled(enabled: Boolean) = launchSaving("saveColorfulPreviewEnabled", "colorful preview setting") { repository.saveColorfulPreviewEnabled(enabled) }

    fun saveGiantWordsEnabled(enabled: Boolean) = launchSaving("saveGiantWordsEnabled", "giant words setting") { repository.saveGiantWordsEnabled(enabled) }

    fun saveKeyboardHeight(height: String) = launchSaving("saveKeyboardHeight", "keyboard height") { repository.saveKeyboardHeight(height) }

    fun saveVibrationStrength(strength: Int) = launchSaving("saveVibrationStrength", "vibration strength") { repository.saveVibrationStrength(strength) }

    fun saveKeySoundVolume(volume: Int) = launchSaving("saveKeySoundVolume", "key sound volume") { repository.saveKeySoundVolume(volume) }

    fun saveNumberRowEnabled(enabled: Boolean) = launchSaving("saveNumberRowEnabled", "number row setting") { repository.saveNumberRowEnabled(enabled) }

    fun saveAutoSuggestionsEnabled(enabled: Boolean) = launchSaving("saveAutoSuggestionsEnabled", "suggestions setting") { repository.saveAutoSuggestionsEnabled(enabled) }

    fun saveSwipeTypingEnabled(enabled: Boolean) = launchSaving("saveSwipeTypingEnabled", "swipe typing setting") { repository.saveSwipeTypingEnabled(enabled) }

    fun saveKeyboardWallpaperPath(path: String) = launchSaving("saveKeyboardWallpaperPath", "wallpaper") { repository.saveKeyboardWallpaperPath(path) }

    fun saveKeyboardWallpaperOpacity(opacity: Int) = launchSaving("saveKeyboardWallpaperOpacity", "wallpaper opacity") { repository.saveKeyboardWallpaperOpacity(opacity) }

    fun saveKeyShape(shape: String) = launchSaving("saveKeyShape", "key shape") { repository.saveKeyShape(shape) }

    fun saveKeyBorderEnabled(enabled: Boolean) = launchSaving("saveKeyBorderEnabled", "key border setting") { repository.saveKeyBorderEnabled(enabled) }

    fun saveKeyBorderThickness(thickness: Int) = launchSaving("saveKeyBorderThickness", "key border thickness") { repository.saveKeyBorderThickness(thickness) }

    fun saveKeyTextSize(size: String) = launchSaving("saveKeyTextSize", "key text size") { repository.saveKeyTextSize(size) }

    fun deleteArt(art: SavedArt) = launchSaving("deleteArt", "changes to your saved art") { deleteArtUseCase(art) }

    fun clearAllArt() = launchSaving("clearAllArt", "changes to your saved art") { repository.clearAllArt() }
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
