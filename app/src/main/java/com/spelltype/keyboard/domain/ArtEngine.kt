package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.FrameStyle

object ArtEngine {
    fun applyFrame(text: String, style: FrameStyle): String {
        if (text.isEmpty()) return ""

        return when (style) {
            FrameStyle.NONE -> text
            FrameStyle.BOX -> formatStandardBox(text, "┌", "─", "┐", "│", "│", "└", "─", "┘")
            FrameStyle.BOX_DOUBLE -> formatStandardBox(text, "╔", "═", "╗", "║", "║", "╚", "═", "╝")
            FrameStyle.BOX_ROUNDED -> formatStandardBox(text, "╭", "─", "╮", "│", "│", "╰", "─", "╯")
            FrameStyle.DOTTED -> formatStandardBox(text, "░", "░", "░", "░", "░", "░", "░", "░")
            FrameStyle.THICK -> formatStandardBox(text, "█", "▄", "█", "█", "█", "█", "▀", "█")
            FrameStyle.DASHED -> formatStandardBox(text, "┌", "╌", "┐", "╎", "╎", "└", "╌", "┘")
            FrameStyle.CORNER -> formatStandardBox(text, "◤", "─", "◥", "│", "│", "◣", "─", "◢")

            FrameStyle.STAR -> formatUniformBorder(text, "★")
            FrameStyle.DIAMOND -> formatUniformBorder(text, "◆")
            FrameStyle.HEARTS -> formatUniformBorder(text, "♥")
            FrameStyle.FLOWERS -> formatUniformBorder(text, "✿")
            FrameStyle.MUSIC -> formatUniformBorder(text, "♪")
            FrameStyle.SNOWFLAKE -> formatUniformBorder(text, "❄")
            FrameStyle.CROSS -> formatUniformBorder(text, "⚔")

            FrameStyle.ARROW -> formatStandardBox(text, "◀", "─", "▶", "│", "│", "◀", "─", "▶")
            FrameStyle.BRACKET -> {
                val lines = text.split("\n")
                lines.joinToString("\n") { "【 $it 】" }
            }

            // Emoji Uniform Borders
            FrameStyle.LEAF -> formatUniformBorder(text, "🍁")
            FrameStyle.SPARKS -> formatUniformBorder(text, "✨")
            FrameStyle.CROWN -> formatUniformBorder(text, "👑")
            FrameStyle.SWIRL -> formatUniformBorder(text, "🌀")
            FrameStyle.MOON -> formatUniformBorder(text, "🌙")
            FrameStyle.SUN -> formatUniformBorder(text, "☀️")
            FrameStyle.GALAXY -> formatUniformBorder(text, "🌌")
            FrameStyle.ANCHOR -> formatUniformBorder(text, "⚓")
            FrameStyle.SKULL -> formatUniformBorder(text, "💀")
            FrameStyle.COFFEE -> formatUniformBorder(text, "☕")
            FrameStyle.FISH -> formatUniformBorder(text, "🐟")
            FrameStyle.CLOUD -> formatUniformBorder(text, "☁️")
            FrameStyle.FIRE -> formatUniformBorder(text, "🔥")
            FrameStyle.PARTY -> formatUniformBorder(text, "🎉")
            FrameStyle.BALLOON -> formatUniformBorder(text, "🎈")
            FrameStyle.GHOST -> formatUniformBorder(text, "👻")
            FrameStyle.FLORAL -> formatUniformBorder(text, "✿")
            FrameStyle.GEM -> formatUniformBorder(text, "💎")
            FrameStyle.CLOVER -> formatUniformBorder(text, "🍀")
            FrameStyle.LINE_BORDER -> formatStandardBox(text, "☰", "☰", "☰", "☰", "☰", "☰", "☰", "☰")
        }
    }

    private fun formatStandardBox(
        text: String,
        topLeft: String, topHorizontal: String, topRight: String,
        leftVertical: String, rightVertical: String,
        bottomLeft: String, bottomHorizontal: String, bottomRight: String
    ): String {
        val lines = text.split("\n")
        val maxLength = lines.maxOf { it.length }
        val top = topLeft + topHorizontal.repeat(maxLength + 2) + topRight
        val middle = lines.joinToString("\n") { line ->
            val padding = " ".repeat(maxLength - line.length)
            "$leftVertical $line$padding $rightVertical"
        }
        val bottom = bottomLeft + bottomHorizontal.repeat(maxLength + 2) + bottomRight
        return "$top\n$middle\n$bottom"
    }

    private fun formatUniformBorder(text: String, borderChar: String): String {
        val lines = text.split("\n")
        val maxLength = lines.maxOf { it.length }
        val top = borderChar.repeat(maxLength + 4)
        val middle = lines.joinToString("\n") { line ->
            val padding = " ".repeat(maxLength - line.length)
            "$borderChar $line$padding $borderChar"
        }
        val bottom = borderChar.repeat(maxLength + 4)
        return "$top\n$middle\n$bottom"
    }
}
