package com.spelltype.keyboard.domain.usecase

import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository

class SaveArtUseCase(private val repository: KeyboardRepository) {
    suspend operator fun invoke(art: SavedArt) = repository.saveArt(art)
}
