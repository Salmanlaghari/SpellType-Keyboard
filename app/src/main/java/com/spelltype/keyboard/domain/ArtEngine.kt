package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.FrameStyle

object ArtEngine {
    fun applyFrame(text: String, style: FrameStyle): String {
        if (text.isEmpty()) return ""

        return when (style) {
            FrameStyle.NONE -> text
            // Box styles — single line with text INSIDE
            FrameStyle.BOX -> "┌─ $text ─┐"
            FrameStyle.BOX_DOUBLE -> "╔═ $text ═╗"
            FrameStyle.BOX_ROUNDED -> "╭─ $text ─╮"
            FrameStyle.DOTTED -> "░░ $text ░░"
            FrameStyle.THICK -> "██ $text ██"
            FrameStyle.DASHED -> "┌╌ $text ╌┐"
            FrameStyle.CORNER -> "◤─ $text ─◥"

            // Symbol borders — text centered inside
            FrameStyle.STAR -> "★★★ $text ★★★"
            FrameStyle.DIAMOND -> "◆◆◆ $text ◆◆◆"
            FrameStyle.HEARTS -> "♥♥♥ $text ♥♥♥"
            FrameStyle.FLOWERS -> "✿✿✿ $text ✿✿✿"
            FrameStyle.MUSIC -> "♪♪♪ $text ♪♪♪"
            FrameStyle.SNOWFLAKE -> "❄❄❄ $text ❄❄❄"
            FrameStyle.CROSS -> "⚔⚔⚔ $text ⚔⚔⚔"

            FrameStyle.ARROW -> "◀══ $text ══▶"
            FrameStyle.BRACKET -> "【 $text 】"

            // Emoji borders — text centered inside
            FrameStyle.LEAF -> "🍁🍁🍁 $text 🍁🍁🍁"
            FrameStyle.SPARKS -> "✨✨✨ $text ✨✨✨"
            FrameStyle.CROWN -> "👑👑👑 $text 👑👑👑"
            FrameStyle.SWIRL -> "🌀🌀🌀 $text 🌀🌀🌀"
            FrameStyle.MOON -> "🌙🌙🌙 $text 🌙🌙🌙"
            FrameStyle.SUN -> "☀️☀️☀️ $text ☀️☀️☀️"
            FrameStyle.GALAXY -> "🌌🌌🌌 $text 🌌🌌🌌"
            FrameStyle.ANCHOR -> "⚓⚓⚓ $text ⚓⚓⚓"
            FrameStyle.SKULL -> "💀💀💀 $text 💀💀💀"
            FrameStyle.COFFEE -> "☕☕☕ $text ☕☕☕"
            FrameStyle.FISH -> "🐟🐟🐟 $text 🐟🐟🐟"
            FrameStyle.CLOUD -> "☁️☁️☁️ $text ☁️☁️☁️"
            FrameStyle.FIRE -> "🔥🔥🔥 $text 🔥🔥🔥"
            FrameStyle.PARTY -> "🎉🎉🎉 $text 🎉🎉🎉"
            FrameStyle.BALLOON -> "🎈🎈🎈 $text 🎈🎈🎈"
            FrameStyle.GHOST -> "👻👻👻 $text 👻👻👻"
            FrameStyle.FLORAL -> "✿✿✿ $text ✿✿✿"
            FrameStyle.GEM -> "💎💎💎 $text 💎💎💎"
            FrameStyle.CLOVER -> "🍀🍀🍀 $text 🍀🍀🍀"
            FrameStyle.LINE_BORDER -> "☰☰☰ $text ☰☰☰"
        }
    }
}
