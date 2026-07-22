package com.spelltype.keyboard.domain.usecase

import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.repository.KeyboardRepository

class ApplyFrameUseCase(private val repository: KeyboardRepository) {
    suspend operator fun invoke(text: String, style: FrameStyle): String {
        if (text.isEmpty()) return ""
        val styledText = ArtEngine.applyFrame(text, style)
        if (style != FrameStyle.NONE) {
            repository.saveArt(
                SavedArt(
                    originalText = text,
                    styledText = styledText,
                    styleName = style.name
                )
            )
        }
        return styledText
    }
}
