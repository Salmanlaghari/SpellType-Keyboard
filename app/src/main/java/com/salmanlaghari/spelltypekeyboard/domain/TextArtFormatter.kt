package com.salmanlaghari.spelltypekeyboard.domain

import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle

/**
 * Single source of truth for the styling pipeline:
 * unicode → glitter → shape → frame → signature.
 */
object TextArtFormatter {

    private val GLITTER_SYMBOLS = listOf("✨", "🌟", "⭐", "💫")

    fun applyGlitter(text: String): String {
        val words = text.split(" ")
        if (words.size == 1) return "✨ $text ✨"

        val sb = StringBuilder()
        for (i in words.indices) {
            sb.append(words[i])
            if (i < words.size - 1) {
                sb.append(" ${GLITTER_SYMBOLS[i % GLITTER_SYMBOLS.size]} ")
            }
        }
        return sb.toString()
    }

    fun format(
        text: String,
        style: FrameStyle = FrameStyle.NONE,
        shape: ShapeLayout = ShapeLayout.NONE,
        unicode: UnicodeStyle = UnicodeStyle.NONE,
        glitterEnabled: Boolean = false,
        signature: String = ""
    ): String {
        if (text.isEmpty()) return ""

        var processed = UnicodeStylingEngine.applyStyle(text, unicode)
        if (glitterEnabled) {
            processed = applyGlitter(processed)
        }
        processed = ShapeEngine.applyShape(processed, shape)
        processed = ArtEngine.applyFrame(processed, style)
        if (signature.isNotEmpty()) {
            processed = "$processed\n$signature"
        }
        return processed
    }

    /** Human readable label for a style enum, e.g. BOX_DOUBLE → "Box double". */
    fun displayName(style: Enum<*>): String =
        style.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
}
