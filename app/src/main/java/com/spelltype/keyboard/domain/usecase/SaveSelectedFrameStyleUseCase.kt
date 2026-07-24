package com.spelltype.keyboard.domain.usecase

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.repository.KeyboardRepository

class SaveSelectedFrameStyleUseCase(private val repository: KeyboardRepository) {
    suspend operator fun invoke(style: FrameStyle) = repository.saveSelectedFrameStyle(style)
}
