package com.spelltype.keyboard.domain.repository

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import kotlinx.coroutines.flow.Flow

import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle

interface KeyboardRepository {
    fun getSavedArtList(): Flow<List<SavedArt>>
    suspend fun saveArt(art: SavedArt)
    suspend fun deleteArt(art: SavedArt)

    fun getSelectedFrameStyle(): Flow<FrameStyle>
    suspend fun saveSelectedFrameStyle(style: FrameStyle)

    fun getSelectedShapeLayout(): Flow<ShapeLayout>
    suspend fun saveSelectedShapeLayout(shape: ShapeLayout)

    fun getSelectedUnicodeStyle(): Flow<UnicodeStyle>
    suspend fun saveSelectedUnicodeStyle(style: UnicodeStyle)

    fun getGlitterEnabled(): Flow<Boolean>
    suspend fun saveGlitterEnabled(enabled: Boolean)

    fun getCustomSignature(): Flow<String>
    suspend fun saveCustomSignature(signature: String)

    fun getFavoriteStyles(): Flow<Set<String>>
    suspend fun saveFavoriteStyles(favorites: Set<String>)

    fun getVibrationEnabled(): Flow<Boolean>
    suspend fun saveVibrationEnabled(enabled: Boolean)

    fun getSoundEnabled(): Flow<Boolean>
    suspend fun saveSoundEnabled(enabled: Boolean)

    fun getThemeSelection(): Flow<String>
    suspend fun saveThemeSelection(theme: String)

    fun getPremiumUnlocked(): Flow<Boolean>
    suspend fun savePremiumUnlocked(unlocked: Boolean)

    suspend fun clearAllArt()
}
