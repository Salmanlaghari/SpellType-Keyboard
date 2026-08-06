package com.salmanlaghari.spelltypekeyboard.domain.usecase

import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedFrameStyleUseCase(private val repository: KeyboardRepository) {
    operator fun invoke(): Flow<FrameStyle> = repository.getSelectedFrameStyle()
}
