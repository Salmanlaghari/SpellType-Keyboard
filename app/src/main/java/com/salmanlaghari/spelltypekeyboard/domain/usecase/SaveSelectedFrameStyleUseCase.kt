package com.salmanlaghari.spelltypekeyboard.domain.usecase

import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository

class SaveSelectedFrameStyleUseCase(private val repository: KeyboardRepository) {
    suspend operator fun invoke(style: FrameStyle) = repository.saveSelectedFrameStyle(style)
}
