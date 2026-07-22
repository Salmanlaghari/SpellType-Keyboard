package com.spelltype.keyboard.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import com.spelltype.keyboard.domain.usecase.*
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

    val savedArtList: StateFlow<List<SavedArt>> = getSavedArtListUseCase()
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

    fun selectFrameStyle(style: FrameStyle) {
        viewModelScope.launch {
            saveSelectedFrameStyleUseCase(style)
        }
    }

    fun selectShapeLayout(shape: ShapeLayout) {
        viewModelScope.launch {
            repository.saveSelectedShapeLayout(shape)
        }
    }

    fun selectUnicodeStyle(style: UnicodeStyle) {
        viewModelScope.launch {
            repository.saveSelectedUnicodeStyle(style)
        }
    }

    fun setGlitterEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveGlitterEnabled(enabled)
        }
    }

    fun setCustomSignature(signature: String) {
        viewModelScope.launch {
            repository.saveCustomSignature(signature)
        }
    }

    fun toggleFavoriteStyle(style: FrameStyle) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            repository.saveVibrationEnabled(enabled)
        }
    }

    fun saveSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveSoundEnabled(enabled)
        }
    }

    fun setThemeSelection(theme: String) {
        viewModelScope.launch {
            repository.saveThemeSelection(theme)
        }
    }

    fun setPremiumUnlocked(unlocked: Boolean) {
        viewModelScope.launch {
            repository.savePremiumUnlocked(unlocked)
        }
    }

    // Phase 6 Mappings
    fun saveColorfulPreviewEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveColorfulPreviewEnabled(enabled)
        }
    }

    fun saveGiantWordsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveGiantWordsEnabled(enabled)
        }
    }

    fun saveKeyboardHeight(height: String) {
        viewModelScope.launch {
            repository.saveKeyboardHeight(height)
        }
    }

    fun saveVibrationStrength(strength: Int) {
        viewModelScope.launch {
            repository.saveVibrationStrength(strength)
        }
    }

    fun saveKeySoundVolume(volume: Int) {
        viewModelScope.launch {
            repository.saveKeySoundVolume(volume)
        }
    }

    fun saveNumberRowEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveNumberRowEnabled(enabled)
        }
    }

    fun saveAutoSuggestionsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveAutoSuggestionsEnabled(enabled)
        }
    }

    fun saveSwipeTypingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveSwipeTypingEnabled(enabled)
        }
    }

    fun deleteArt(art: SavedArt) {
        viewModelScope.launch {
            deleteArtUseCase(art)
        }
    }

    fun clearAllArt() {
        viewModelScope.launch {
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
