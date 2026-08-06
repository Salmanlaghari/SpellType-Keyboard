package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.FrameStyle

object ArtEngine {
    fun applyFrame(text: String, style: FrameStyle): String {
        if (text.isEmpty()) return ""

        return when (style) {
            FrameStyle.NONE -> text
            // ═══ Box styles ═══
            FrameStyle.BOX -> "┌─ $text ─┐"
            FrameStyle.BOX_DOUBLE -> "╔═ $text ═╗"
            FrameStyle.BOX_ROUNDED -> "╭─ $text ─╮"
            FrameStyle.DOTTED -> "░░ $text ░░"
            FrameStyle.THICK -> "██ $text ██"
            FrameStyle.DASHED -> "┌╌ $text ╌┐"
            FrameStyle.CORNER -> "◤─ $text ─◥"

            // ═══ Symbol borders ═══
            FrameStyle.STAR -> "★★★ $text ★★★"
            FrameStyle.DIAMOND -> "◆◆◆ $text ◆◆◆"
            FrameStyle.HEARTS -> "♥♥♥ $text ♥♥♥"
            FrameStyle.FLOWERS -> "✿✿✿ $text ✿✿✿"
            FrameStyle.MUSIC -> "♪♪♪ $text ♪♪♪"
            FrameStyle.SNOWFLAKE -> "❄❄❄ $text ❄❄❄"
            FrameStyle.CROSS -> "⚔⚔⚔ $text ⚔⚔⚔"
            FrameStyle.ARROW -> "◀══ $text ══▶"
            FrameStyle.BRACKET -> "【 $text 】"

            // ═══ Emoji borders ═══
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

            // ═══════════════════════════════════════════════════
            //  50+ CREATIVE KEYBOARD STYLES
            // ═══════════════════════════════════════════════════

            // 💔 Heart Alphabet — each letter wrapped in hearts
            FrameStyle.HEART_ALPHABET -> {
                text.map { c -> "♥$c♥" }.joinToString("")
            }

            // 💕 Love Mode — full love decoration
            FrameStyle.LOVE_MODE -> "💕❤️💕 $text 💕❤️💕"

            // 💔 Break Mode — broken heart style
            FrameStyle.BREAK_MODE -> {
                "💔 $text 💔".replace(" ", " 💔 ")
            }

            // 🔥 Revenge Mode — aggressive bold with fire
            FrameStyle.REVENGE_MODE -> "🔥💢🔥 $text 🔥💢🔥"

            // 🌧️ 5D Rain Mode — rain drops around text
            FrameStyle.RAIN_MODE -> {
                val rain = listOf("🌧️", "💧", "💦", "🌊")
                val drops = (1..3).map { rain.random() }.joinToString("")
                "$drops $text $drops"
            }

            // 🌸 Cherry Blossom — sakura petals
            FrameStyle.CHERRY_BLOSSOM -> "🌸🌷🌸 $text 🌸🌷🌸"

            // ❄️ Ice Mode — frozen text
            FrameStyle.ICE_MODE -> "❄️🧊❄️ $text ❄️🧊❄️"

            // 🌈 Rainbow Mode — rainbow colors
            FrameStyle.RAINBOW_MODE -> "🌈🎨🌈 $text 🌈🎨🌈"

            // ⚡ Thunder Mode — electric bolts
            FrameStyle.THUNDER_MODE -> "⚡💥⚡ $text ⚡💥⚡"

            // 🌙 Night Mode — moonlight
            FrameStyle.NIGHT_MODE -> "🌙✨🌙 $text 🌙✨🌙"

            // 🔮 Crystal Mode — crystal clear
            FrameStyle.CRYSTAL_MODE -> "🔮💎🔮 $text 🔮💎🔮"

            // 🦋 Butterfly Mode — butterflies
            FrameStyle.BUTTERFLY_MODE -> "🦋🌸🦋 $text 🦋🌸🦋"

            // 🌺 Rose Mode — rose petals
            FrameStyle.ROSE_MODE -> "🌹🌺🌹 $text 🌹🌺🌹"

            // 💎 Diamond Mode — diamond encrusted
            FrameStyle.DIAMOND_MODE -> "💎💠💎 $text 💎💠💎"

            // 🎭 Shadow Mode — shadow effect (text with shadow chars)
            FrameStyle.SHADOW_MODE -> {
                text.map { c -> "$c" }.joinToString("░")
            }

            // 🔥 Flame Mode — fire text
            FrameStyle.FLAME_MODE -> "🔥🔥🔥 $text 🔥🔥🔥"

            // ⭐ Starry Mode — starry night
            FrameStyle.STARRY_MODE -> "⭐🌟⭐ $text ⭐🌟⭐"

            // 🌊 Wave Mode — ocean waves
            FrameStyle.WAVE_MODE -> "🌊🐚🌊 $text 🌊🐚🌊"

            // 🍂 Autumn Mode — fall leaves
            FrameStyle.AUTUMN_MODE -> "🍂🍁🍂 $text 🍂🍁🍂"

            // ❄️ Winter Mode — winter frost
            FrameStyle.WINTER_MODE -> "❄️☃️❄️ $text ❄️☃️❄️"

            // 🌸 Spring Mode — spring flowers
            FrameStyle.SPRING_MODE -> "🌸🌼🌸 $text 🌸🌼🌸"

            // ☀️ Summer Mode — summer vibes
            FrameStyle.SUMMER_MODE -> "☀️🏖️☀️ $text ☀️🏖️☀️"

            // 🎃 Halloween Mode — spooky
            FrameStyle.HALLOWEEN_MODE -> "🎃👻🎃 $text 🎃👻🎃"

            // 🎄 Christmas Mode — festive
            FrameStyle.CHRISTMAS_MODE -> "🎄🎅🎄 $text 🎄🎅🎄"

            // 💀 Gothic Mode — dark gothic
            FrameStyle.GOTHIC_MODE -> "💀🖤💀 $text 💀🖤💀"

            // 🏰 Medieval Mode — medieval style
            FrameStyle.MEDIEVAL_MODE -> "🏰⚔️🏰 $text 🏰⚔️🏰"

            // 🚀 Space Mode — cosmic style
            FrameStyle.SPACE_MODE -> "🚀🪐🚀 $text 🚀🪐🚀"

            // 🎮 Gamer Mode — gaming style
            FrameStyle.GAMER_MODE -> "🎮🕹️🎮 $text 🎮🕹️🎮"

            // 🎵 Music Mode — musical notes
            FrameStyle.MUSIC_MODE -> "🎵🎶🎵 $text 🎵🎶🎵"

            // 🌴 Tropical Mode — tropical vibes
            FrameStyle.TROPICAL_MODE -> "🌴🌺🌴 $text 🌴🌺🌴"

            // 🐾 Animal Mode — animal themed
            FrameStyle.ANIMAL_MODE -> "🐾🦁🐾 $text 🐾🦁🐾"

            // 🍕 Food Mode — food themed
            FrameStyle.FOOD_MODE -> "🍕🍔🍕 $text 🍕🍔🍕"

            // ⚽ Sports Mode — sports themed
            FrameStyle.SPORTS_MODE -> "⚽🏀⚽ $text ⚽🏀⚽"

            // 🎨 Art Mode — artistic style
            FrameStyle.ART_MODE -> "🎨🖼️🎨 $text 🎨🖼️🎨"

            // 💻 Tech Mode — tech/cyber style
            FrameStyle.TECH_MODE -> "💻⌨️💻 $text 💻⌨️💻"

            // 🌍 World Mode — world themed
            FrameStyle.WORLD_MODE -> "🌍🌎🌍 $text 🌍🌎🌍"

            // 🎯 Target Mode — target/bullseye
            FrameStyle.TARGET_MODE -> "🎯🏹🎯 $text 🎯🏹🎯"

            // 🔑 Key Mode — key/lock themed
            FrameStyle.KEY_MODE -> "🔑🔒🔑 $text 🔑🔒🔑"

            // 💰 Money Mode — money themed
            FrameStyle.MONEY_MODE -> "💰💸💰 $text 💰💸💰"

            // 🎁 Gift Mode — gift/box themed
            FrameStyle.GIFT_MODE -> "🎁🎀🎁 $text 🎁🎀🎁"

            // 🌈 Pride Mode — rainbow pride
            FrameStyle.PRIDE_MODE -> "🌈🏳️‍🌈🌈 $text 🌈🏳️‍🌈🌈"

            // 🕶️ Stealth Mode — stealth/camo
            FrameStyle.STEALTH_MODE -> "🕶️🖤🕶️ $text 🕶️🖤🕶️"

            // 🎪 Circus Mode — circus themed
            FrameStyle.CIRCUS_MODE -> "🎪🤡🎪 $text 🎪🤡🎪"

            // 🏔️ Mountain Mode — mountain/nature
            FrameStyle.MOUNTAIN_MODE -> "🏔️🌲🏔️ $text 🏔️🌲🏔️"

            // 🌅 Sunset Mode — sunset colors
            FrameStyle.SUNSET_MODE -> "🌅🌇🌅 $text 🌅🌇🌅"

            // 🌌 Aurora Mode — aurora borealis
            FrameStyle.AURORA_MODE -> "🌌💫🌌 $text 🌌💫🌌"

            // 🔥 Phoenix Mode — phoenix rebirth
            FrameStyle.PHOENIX_MODE -> "🔥🦅🔥 $text 🔥🦅🔥"

            // 🐉 Dragon Mode — dragon themed
            FrameStyle.DRAGON_MODE -> "🐉🔥🐉 $text 🐉🔥🐉"

            // 👻 Haunted Mode — haunted/ghost
            FrameStyle.HAUNTED_MODE -> "👻🏚️👻 $text 👻🏚️👻"

            // 🎭 Drama Mode — theatrical
            FrameStyle.DRAMA_MODE -> "🎭🎪🎭 $text 🎭🎪🎭"
        }
    }

    /** Get all style names for display */
    fun getAllStyleNames(): List<Pair<FrameStyle, String>> {
        return FrameStyle.values().map { style ->
            style to when (style) {
                FrameStyle.NONE -> "Normal"
                FrameStyle.BOX -> "Box"
                FrameStyle.BOX_DOUBLE -> "Double Box"
                FrameStyle.BOX_ROUNDED -> "Rounded Box"
                FrameStyle.DOTTED -> "Dotted"
                FrameStyle.THICK -> "Thick"
                FrameStyle.DASHED -> "Dashed"
                FrameStyle.CORNER -> "Corner"
                FrameStyle.STAR -> "Star"
                FrameStyle.DIAMOND -> "Diamond"
                FrameStyle.HEARTS -> "Hearts"
                FrameStyle.FLOWERS -> "Flowers"
                FrameStyle.MUSIC -> "Music"
                FrameStyle.SNOWFLAKE -> "Snowflake"
                FrameStyle.CROSS -> "Cross"
                FrameStyle.ARROW -> "Arrow"
                FrameStyle.BRACKET -> "Bracket"
                FrameStyle.LEAF -> "Leaf"
                FrameStyle.SPARKS -> "Sparks"
                FrameStyle.CROWN -> "Crown"
                FrameStyle.SWIRL -> "Swirl"
                FrameStyle.MOON -> "Moon"
                FrameStyle.SUN -> "Sun"
                FrameStyle.GALAXY -> "Galaxy"
                FrameStyle.ANCHOR -> "Anchor"
                FrameStyle.SKULL -> "Skull"
                FrameStyle.COFFEE -> "Coffee"
                FrameStyle.FISH -> "Fish"
                FrameStyle.CLOUD -> "Cloud"
                FrameStyle.FIRE -> "Fire"
                FrameStyle.PARTY -> "Party"
                FrameStyle.BALLOON -> "Balloon"
                FrameStyle.GHOST -> "Ghost"
                FrameStyle.FLORAL -> "Floral"
                FrameStyle.GEM -> "Gem"
                FrameStyle.CLOVER -> "Clover"
                FrameStyle.LINE_BORDER -> "Line Border"
                // New 50+ styles
                FrameStyle.HEART_ALPHABET -> "♥ Heart Alpha"
                FrameStyle.LOVE_MODE -> "💕 Love"
                FrameStyle.BREAK_MODE -> "💔 Break"
                FrameStyle.REVENGE_MODE -> "🔥 Revenge"
                FrameStyle.RAIN_MODE -> "🌧️ 5D Rain"
                FrameStyle.CHERRY_BLOSSOM -> "🌸 Cherry"
                FrameStyle.ICE_MODE -> "❄️ Ice"
                FrameStyle.RAINBOW_MODE -> "🌈 Rainbow"
                FrameStyle.THUNDER_MODE -> "⚡ Thunder"
                FrameStyle.NIGHT_MODE -> "🌙 Night"
                FrameStyle.CRYSTAL_MODE -> "🔮 Crystal"
                FrameStyle.BUTTERFLY_MODE -> "🦋 Butterfly"
                FrameStyle.ROSE_MODE -> "🌹 Rose"
                FrameStyle.DIAMOND_MODE -> "💎 Diamond"
                FrameStyle.SHADOW_MODE -> "🎭 Shadow"
                FrameStyle.FLAME_MODE -> "🔥 Flame"
                FrameStyle.STARRY_MODE -> "⭐ Starry"
                FrameStyle.WAVE_MODE -> "🌊 Wave"
                FrameStyle.AUTUMN_MODE -> "🍂 Autumn"
                FrameStyle.WINTER_MODE -> "❄️ Winter"
                FrameStyle.SPRING_MODE -> "🌸 Spring"
                FrameStyle.SUMMER_MODE -> "☀️ Summer"
                FrameStyle.HALLOWEEN_MODE -> "🎃 Halloween"
                FrameStyle.CHRISTMAS_MODE -> "🎄 Christmas"
                FrameStyle.GOTHIC_MODE -> "💀 Gothic"
                FrameStyle.MEDIEVAL_MODE -> "🏰 Medieval"
                FrameStyle.SPACE_MODE -> "🚀 Space"
                FrameStyle.GAMER_MODE -> "🎮 Gamer"
                FrameStyle.MUSIC_MODE -> "🎵 Music"
                FrameStyle.TROPICAL_MODE -> "🌴 Tropical"
                FrameStyle.ANIMAL_MODE -> "🐾 Animal"
                FrameStyle.FOOD_MODE -> "🍕 Food"
                FrameStyle.SPORTS_MODE -> "⚽ Sports"
                FrameStyle.ART_MODE -> "🎨 Art"
                FrameStyle.TECH_MODE -> "💻 Tech"
                FrameStyle.WORLD_MODE -> "🌍 World"
                FrameStyle.TARGET_MODE -> "🎯 Target"
                FrameStyle.KEY_MODE -> "🔑 Key"
                FrameStyle.MONEY_MODE -> "💰 Money"
                FrameStyle.GIFT_MODE -> "🎁 Gift"
                FrameStyle.PRIDE_MODE -> "🌈 Pride"
                FrameStyle.STEALTH_MODE -> "🕶️ Stealth"
                FrameStyle.CIRCUS_MODE -> "🎪 Circus"
                FrameStyle.MOUNTAIN_MODE -> "🏔️ Mountain"
                FrameStyle.SUNSET_MODE -> "🌅 Sunset"
                FrameStyle.AURORA_MODE -> "🌌 Aurora"
                FrameStyle.PHOENIX_MODE -> "🔥 Phoenix"
                FrameStyle.DRAGON_MODE -> "🐉 Dragon"
                FrameStyle.HAUNTED_MODE -> "👻 Haunted"
                FrameStyle.DRAMA_MODE -> "🎭 Drama"
            }
        }
    }
}
