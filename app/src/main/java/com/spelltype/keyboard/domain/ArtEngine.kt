package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.FrameStyle

object ArtEngine {
    fun applyFrame(text: String, style: FrameStyle): String {
        if (text.isEmpty()) return ""

        return when (style) {
            FrameStyle.NONE -> text
            FrameStyle.BOX -> {
                val lines = text.split("\n")
                val maxLength = lines.maxOf { it.length }
                val top = "┌" + "─".repeat(maxLength + 2) + "┐"
                val middle = lines.joinToString("\n") { line ->
                    val padding = " ".repeat(maxLength - line.length)
                    "│ $line$padding │"
                }
                val bottom = "└" + "─".repeat(maxLength + 2) + "┘"
                "$top\n$middle\n$bottom"
            }
            FrameStyle.STAR -> {
                val lines = text.split("\n")
                val maxLength = lines.maxOf { it.length }
                val top = "★".repeat(maxLength + 4)
                val middle = lines.joinToString("\n") { line ->
                    val padding = " ".repeat(maxLength - line.length)
                    "★ $line$padding ★"
                }
                val bottom = "★".repeat(maxLength + 4)
                "$top\n$middle\n$bottom"
            }
            FrameStyle.BRACKET -> {
                val lines = text.split("\n")
                lines.joinToString("\n") { "【 $it 】" }
            }
            FrameStyle.DIAMOND -> {
                val lines = text.split("\n")
                val maxLength = lines.maxOf { it.length }
                val top = "◆".repeat(maxLength + 4)
                val middle = lines.joinToString("\n") { line ->
                    val padding = " ".repeat(maxLength - line.length)
                    "◆ $line$padding ◆"
                }
                val bottom = "◆".repeat(maxLength + 4)
                "$top\n$middle\n$bottom"
            }
        }
    }
}
