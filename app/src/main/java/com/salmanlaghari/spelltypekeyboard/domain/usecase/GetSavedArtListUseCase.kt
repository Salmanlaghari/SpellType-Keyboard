package com.salmanlaghari.spelltypekeyboard.domain.usecase

import com.salmanlaghari.spelltypekeyboard.domain.model.SavedArt
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow

class GetSavedArtListUseCase(private val repository: KeyboardRepository) {
    operator fun invoke(): Flow<List<SavedArt>> = repository.getSavedArtList()
}
