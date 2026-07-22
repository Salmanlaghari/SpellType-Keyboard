package com.spelltype.keyboard.domain.usecase

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedFrameStyleUseCase(private val repository: KeyboardRepository) {
    operator fun invoke(): Flow<FrameStyle> = repository.getSelectedFrameStyle()
}
