package com.spelltype.keyboard.domain.usecase

import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow

class GetSavedArtListUseCase(private val repository: KeyboardRepository) {
    operator fun invoke(): Flow<List<SavedArt>> = repository.getSavedArtList()
}
