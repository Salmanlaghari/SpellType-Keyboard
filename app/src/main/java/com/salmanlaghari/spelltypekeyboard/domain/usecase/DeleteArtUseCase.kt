package com.salmanlaghari.spelltypekeyboard.domain.usecase

import com.salmanlaghari.spelltypekeyboard.domain.model.SavedArt
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository

class DeleteArtUseCase(private val repository: KeyboardRepository) {
    suspend operator fun invoke(art: SavedArt) = repository.deleteArt(art)
}
