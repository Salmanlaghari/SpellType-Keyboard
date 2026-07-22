package com.spelltype.keyboard

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeKeyboardRepository : KeyboardRepository {

    private val savedArtList = mutableListOf<SavedArt>()
    private val savedArtFlow = MutableStateFlow<List<SavedArt>>(emptyList())
    private val selectedFrameStyleFlow = MutableStateFlow(FrameStyle.NONE)

    override fun getSavedArtList(): Flow<List<SavedArt>> {
        return savedArtFlow
    }

    override suspend fun saveArt(art: SavedArt) {
        val newArt = art.copy(id = savedArtList.size + 1)
        savedArtList.add(newArt)
        savedArtFlow.value = savedArtList.toList().reversed() // reverse chronological
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

    override suspend fun clearAllArt() {
        savedArtList.clear()
        savedArtFlow.value = emptyList()
    }
}
