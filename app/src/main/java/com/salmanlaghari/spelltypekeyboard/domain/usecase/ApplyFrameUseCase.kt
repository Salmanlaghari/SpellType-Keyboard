package com.salmanlaghari.spelltypekeyboard.domain.usecase

import com.salmanlaghari.spelltypekeyboard.domain.ArtEngine
import com.salmanlaghari.spelltypekeyboard.domain.ShapeEngine
import com.salmanlaghari.spelltypekeyboard.domain.UnicodeStylingEngine
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

        // 1. Unicode Styling
        var processed = UnicodeStylingEngine.applyStyle(text, unicode)

        // 2. Glitter Effect (interspersing decorative symbols between words)
        if (glitterEnabled) {
            val glitterSymbols = listOf("✨", "🌟", "⭐", "💫")
            val words = processed.split(" ")
            val sb = StringBuilder()
            for (i in words.indices) {
                sb.append(words[i])
                if (i < words.size - 1) {
                    val symbol = glitterSymbols[i % glitterSymbols.size]
                    sb.append(" $symbol ")
                }
            }
            // If it is a single word, wrap it with sparkles
            if (words.size == 1) {
                processed = "✨ $processed ✨"
            } else {
                processed = sb.toString()
            }
        }

        // 3. Shape Layout
        processed = ShapeEngine.applyShape(processed, shape)

        // 4. Frame Style
        processed = ArtEngine.applyFrame(processed, style)

        // 5. Custom Signature
        if (signature.isNotEmpty()) {
            processed = "$processed\n$signature"
        }

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
