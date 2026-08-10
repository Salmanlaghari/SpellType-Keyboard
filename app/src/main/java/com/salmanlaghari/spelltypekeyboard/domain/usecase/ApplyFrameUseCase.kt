package com.salmanlaghari.spelltypekeyboard.domain.usecase

import com.salmanlaghari.spelltypekeyboard.domain.TextArtFormatter
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.SavedArt
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository

class ApplyFrameUseCase(private val repository: KeyboardRepository) {
    suspend operator fun invoke(
        text: String,
        style: FrameStyle,
        shape: ShapeLayout = ShapeLayout.NONE,
        unicode: UnicodeStyle = UnicodeStyle.NONE,
        glitterEnabled: Boolean = false,
        signature: String = ""
    ): String {
        if (text.isEmpty()) return ""

        val processed = TextArtFormatter.format(
            text = text,
            style = style,
            shape = shape,
            unicode = unicode,
            glitterEnabled = glitterEnabled,
            signature = signature
        )

        // Save to database if any formatting is applied
        if (style != FrameStyle.NONE || shape != ShapeLayout.NONE || unicode != UnicodeStyle.NONE || glitterEnabled) {
            repository.saveArt(
                SavedArt(
                    originalText = text,
                    styledText = processed,
                    styleName = when {
                        style != FrameStyle.NONE -> style.name
                        shape != ShapeLayout.NONE -> "SHAPE_${shape.name}"
                        unicode != UnicodeStyle.NONE -> "UNICODE_${unicode.name}"
                        else -> "GLITTER"
                    }
                )
            )
        }

        return processed
    }
}
