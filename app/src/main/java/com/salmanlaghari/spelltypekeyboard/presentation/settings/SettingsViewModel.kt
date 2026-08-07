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
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedFrameStyle: StateFlow<FrameStyle> = getSelectedFrameStyleUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FrameStyle.NONE)

    val selectedShapeLayout: StateFlow<ShapeLayout> = repository.getSelectedShapeLayout()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ShapeLayout.NONE)

    val selectedUnicodeStyle: StateFlow<UnicodeStyle> = repository.getSelectedUnicodeStyle()
        .stateIn(viewModelScope, SharingStarted.Eagerly, UnicodeStyle.NONE)

    val glitterEnabled: StateFlow<Boolean> = repository.getGlitterEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val customSignature: StateFlow<String> = repository.getCustomSignature()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val favoriteStyles: StateFlow<Set<String>> = repository.getFavoriteStyles()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val vibrationEnabled: StateFlow<Boolean> = repository.getVibrationEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val soundEnabled: StateFlow<Boolean> = repository.getSoundEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val themeSelection: StateFlow<String> = repository.getThemeSelection()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "DARK")

    val premiumUnlocked: StateFlow<Boolean> = repository.getPremiumUnlocked()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Phase 6 Flow Exposes
    val colorfulPreviewEnabled: StateFlow<Boolean> = repository.getColorfulPreviewEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val giantWordsEnabled: StateFlow<Boolean> = repository.getGiantWordsEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val keyboardHeight: StateFlow<String> = repository.getKeyboardHeight()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "MEDIUM")

    val vibrationStrength: StateFlow<Int> = repository.getVibrationStrength()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)

    val keySoundVolume: StateFlow<Int> = repository.getKeySoundVolume()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)

    val numberRowEnabled: StateFlow<Boolean> = repository.getNumberRowEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val autoSuggestionsEnabled: StateFlow<Boolean> = repository.getAutoSuggestionsEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val swipeTypingEnabled: StateFlow<Boolean> = repository.getSwipeTypingEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Premium Configurations StateFlows
    val keyboardWallpaperPath: StateFlow<String> = repository.getKeyboardWallpaperPath()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val keyboardWallpaperOpacity: StateFlow<Int> = repository.getKeyboardWallpaperOpacity()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)

    val keyShape: StateFlow<String> = repository.getKeyShape()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "ROUNDED")

    val keyBorderEnabled: StateFlow<Boolean> = repository.getKeyBorderEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val keyBorderThickness: StateFlow<Int> = repository.getKeyBorderThickness()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    val keyTextSize: StateFlow<String> = repository.getKeyTextSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "MEDIUM")

    fun selectFrameStyle(style: FrameStyle) {
        launchSaving("selectFrameStyle", "frame style") {
            saveSelectedFrameStyleUseCase(style)
        }
    }

    fun selectShapeLayout(shape: ShapeLayout) {
        launchSaving("selectShapeLayout", "shape layout") {
            repository.saveSelectedShapeLayout(shape)
        }
    }

    fun selectUnicodeStyle(style: UnicodeStyle) {
        launchSaving("selectUnicodeStyle", "text style") {
            repository.saveSelectedUnicodeStyle(style)
        }
    }

    fun setGlitterEnabled(enabled: Boolean) {
        launchSaving("setGlitterEnabled", "glitter setting") {
            repository.saveGlitterEnabled(enabled)
        }
    }

    fun setCustomSignature(signature: String) {
        launchSaving("setCustomSignature", "signature") {
            repository.saveCustomSignature(signature)
        }
    }

    fun toggleFavoriteStyle(style: FrameStyle) {
        launchSaving("toggleFavoriteStyle", "favourites") {
            val current = favoriteStyles.value.toMutableSet()
            if (current.contains(style.name)) {
                current.remove(style.name)
            } else {
                current.add(style.name)
            }
            repository.saveFavoriteStyles(current)
        }
    }

    // Alignment setters matching SettingsActivity.kt exactly
    fun saveVibrationEnabled(enabled: Boolean) {
        launchSaving("saveVibrationEnabled", "vibration setting") {
            repository.saveVibrationEnabled(enabled)
        }
    }

    fun saveSoundEnabled(enabled: Boolean) {
        launchSaving("saveSoundEnabled", "sound setting") {
            repository.saveSoundEnabled(enabled)
        }
    }

    fun setThemeSelection(theme: String) {
        launchSaving("setThemeSelection", "theme") {
            repository.saveThemeSelection(theme)
        }
    }

    fun setPremiumUnlocked(unlocked: Boolean) {
        launchSaving("setPremiumUnlocked", "premium status") {
            repository.savePremiumUnlocked(unlocked)
        }
    }

    // Phase 6 Mappings
    fun saveColorfulPreviewEnabled(enabled: Boolean) {
        launchSaving("saveColorfulPreviewEnabled", "colorful preview setting") {
            repository.saveColorfulPreviewEnabled(enabled)
        }
    }

    fun saveGiantWordsEnabled(enabled: Boolean) {
        launchSaving("saveGiantWordsEnabled", "giant words setting") {
            repository.saveGiantWordsEnabled(enabled)
        }
    }

    fun saveKeyboardHeight(height: String) {
        launchSaving("saveKeyboardHeight", "keyboard height") {
            repository.saveKeyboardHeight(height)
        }
    }

    fun saveVibrationStrength(strength: Int) {
        launchSaving("saveVibrationStrength", "vibration strength") {
            repository.saveVibrationStrength(strength)
        }
    }

    fun saveKeySoundVolume(volume: Int) {
        launchSaving("saveKeySoundVolume", "key sound volume") {
            repository.saveKeySoundVolume(volume)
        }
    }

    fun saveNumberRowEnabled(enabled: Boolean) {
        launchSaving("saveNumberRowEnabled", "number row setting") {
            repository.saveNumberRowEnabled(enabled)
        }
    }

    fun saveAutoSuggestionsEnabled(enabled: Boolean) {
        launchSaving("saveAutoSuggestionsEnabled", "suggestions setting") {
            repository.saveAutoSuggestionsEnabled(enabled)
        }
    }

    fun saveSwipeTypingEnabled(enabled: Boolean) {
        launchSaving("saveSwipeTypingEnabled", "swipe typing setting") {
            repository.saveSwipeTypingEnabled(enabled)
        }
    }

    fun saveKeyboardWallpaperPath(path: String) {
        launchSaving("saveKeyboardWallpaperPath", "wallpaper") {
            repository.saveKeyboardWallpaperPath(path)
        }
    }

    fun saveKeyboardWallpaperOpacity(opacity: Int) {
        launchSaving("saveKeyboardWallpaperOpacity", "wallpaper opacity") {
            repository.saveKeyboardWallpaperOpacity(opacity)
        }
    }

    fun saveKeyShape(shape: String) {
        launchSaving("saveKeyShape", "key shape") {
            repository.saveKeyShape(shape)
        }
    }

    fun saveKeyBorderEnabled(enabled: Boolean) {
        launchSaving("saveKeyBorderEnabled", "key border setting") {
            repository.saveKeyBorderEnabled(enabled)
        }
    }

    fun saveKeyBorderThickness(thickness: Int) {
        launchSaving("saveKeyBorderThickness", "key border thickness") {
            repository.saveKeyBorderThickness(thickness)
        }
    }

    fun saveKeyTextSize(size: String) {
        launchSaving("saveKeyTextSize", "key text size") {
            repository.saveKeyTextSize(size)
        }
    }

    fun deleteArt(art: SavedArt) {
        launchSaving("deleteArt", "changes to your saved art") {
            deleteArtUseCase(art)
        }
    }

    fun clearAllArt() {
        launchSaving("clearAllArt", "changes to your saved art") {
            repository.clearAllArt()
        }
    }
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
