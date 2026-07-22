package com.spelltype.keyboard.domain.repository

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import kotlinx.coroutines.flow.Flow

interface KeyboardRepository {
    fun getSavedArtList(): Flow<List<SavedArt>>
    suspend fun saveArt(art: SavedArt)
    suspend fun deleteArt(art: SavedArt)

    fun getSelectedFrameStyle(): Flow<FrameStyle>
    suspend fun saveSelectedFrameStyle(style: FrameStyle)
    suspend fun clearAllArt()
}
