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
