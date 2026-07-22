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

    override suspend fun clearAllArt() {
        savedArtList.clear()
        savedArtFlow.value = emptyList()
    }
}
