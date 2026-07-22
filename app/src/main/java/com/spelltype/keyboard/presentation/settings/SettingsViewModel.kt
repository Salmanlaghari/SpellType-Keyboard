package com.spelltype.keyboard.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.spelltype.keyboard.domain.model.FrameStyle
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

    fun selectFrameStyle(style: FrameStyle) {
        viewModelScope.launch {
            saveSelectedFrameStyleUseCase(style)
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
