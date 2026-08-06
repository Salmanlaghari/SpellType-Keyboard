package com.salmanlaghari.spelltypekeyboard.domain

import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle

object StyleCategorizer {

    private val classicStyles = setOf(
        FrameStyle.NONE, FrameStyle.BOX, FrameStyle.BOX_DOUBLE, FrameStyle.BOX_ROUNDED,
        FrameStyle.DOTTED, FrameStyle.DASHED, FrameStyle.CORNER, FrameStyle.BRACKET
    )

    private val symbolStyles = setOf(
        FrameStyle.STAR, FrameStyle.DIAMOND, FrameStyle.HEARTS, FrameStyle.FLOWERS,
        FrameStyle.MUSIC, FrameStyle.SNOWFLAKE, FrameStyle.CROSS, FrameStyle.ARROW,
        FrameStyle.THICK, FrameStyle.LINE_BORDER
    )

    private val emojiStyles = setOf(
        FrameStyle.LEAF, FrameStyle.SPARKS, FrameStyle.CROWN, FrameStyle.SWIRL,
        FrameStyle.MOON, FrameStyle.SUN, FrameStyle.GALAXY, FrameStyle.ANCHOR,
        FrameStyle.SKULL, FrameStyle.COFFEE, FrameStyle.FISH, FrameStyle.CLOUD,
        FrameStyle.FIRE, FrameStyle.PARTY, FrameStyle.BALLOON, FrameStyle.GHOST,
        FrameStyle.FLORAL, FrameStyle.GEM, FrameStyle.CLOVER
    )

    // ═══ NEW Creative Categories ═══
    private val heartLoveStyles = setOf(
        FrameStyle.HEART_ALPHABET, FrameStyle.LOVE_MODE, FrameStyle.BREAK_MODE,
        FrameStyle.ROSE_MODE, FrameStyle.PRIDE_MODE
    )

    private val natureStyles = setOf(
        FrameStyle.CHERRY_BLOSSOM, FrameStyle.BUTTERFLY_MODE, FrameStyle.TROPICAL_MODE,
        FrameStyle.AUTUMN_MODE, FrameStyle.SPRING_MODE, FrameStyle.SUMMER_MODE,
        FrameStyle.WINTER_MODE, FrameStyle.MOUNTAIN_MODE, FrameStyle.ANIMAL_MODE
    )

    private val weatherStyles = setOf(
        FrameStyle.RAIN_MODE, FrameStyle.ICE_MODE, FrameStyle.THUNDER_MODE,
        FrameStyle.STARRY_MODE, FrameStyle.WAVE_MODE, FrameStyle.AURORA_MODE,
        FrameStyle.SUNSET_MODE
    )

    private val darkStyles = setOf(
        FrameStyle.REVENGE_MODE, FrameStyle.GOTHIC_MODE, FrameStyle.SHADOW_MODE,
        FrameStyle.HAUNTED_MODE, FrameStyle.STEALTH_MODE, FrameStyle.NIGHT_MODE
    )

    private val festiveStyles = setOf(
        FrameStyle.HALLOWEEN_MODE, FrameStyle.CHRISTMAS_MODE, FrameStyle.CIRCUS_MODE,
        FrameStyle.DRAMA_MODE, FrameStyle.GIFT_MODE
    )

    private val powerStyles = setOf(
        FrameStyle.FLAME_MODE, FrameStyle.DRAGON_MODE, FrameStyle.PHOENIX_MODE,
        FrameStyle.SPACE_MODE, FrameStyle.MEDIEVAL_MODE
    )

    private val modernStyles = setOf(
        FrameStyle.GAMER_MODE, FrameStyle.TECH_MODE, FrameStyle.TARGET_MODE,
        FrameStyle.RAINBOW_MODE, FrameStyle.CRYSTAL_MODE, FrameStyle.DIAMOND_MODE
    )

    private val funStyles = setOf(
        FrameStyle.FOOD_MODE, FrameStyle.SPORTS_MODE, FrameStyle.MUSIC_MODE,
        FrameStyle.ART_MODE, FrameStyle.WORLD_MODE, FrameStyle.KEY_MODE,
        FrameStyle.MONEY_MODE
    )

    private val premiumStyles = setOf(
        FrameStyle.GEM, FrameStyle.CROWN, FrameStyle.GALAXY, FrameStyle.FIRE,
        FrameStyle.BOX_DOUBLE, FrameStyle.HEART_ALPHABET, FrameStyle.LOVE_MODE,
        FrameStyle.RAIN_MODE, FrameStyle.CHERRY_BLOSSOM, FrameStyle.DRAGON_MODE,
        FrameStyle.PHOENIX_MODE, FrameStyle.AURORA_MODE, FrameStyle.CRYSTAL_MODE
    )

    fun getCategory(style: FrameStyle): String {
        return when {
            style in classicStyles -> "Classic"
            style in symbolStyles -> "Symbol"
            style in emojiStyles -> "Emoji"
            style in heartLoveStyles -> "♥ Love"
            style in natureStyles -> "🌿 Nature"
            style in weatherStyles -> "🌧️ Weather"
            style in darkStyles -> "💀 Dark"
            style in festiveStyles -> "🎉 Festival"
            style in powerStyles -> "🔥 Power"
            style in modernStyles -> "🎮 Modern"
            style in funStyles -> "🎈 Fun"
            else -> "✨ Other"
        }
    }

    fun isPremium(style: FrameStyle): Boolean {
        return style in premiumStyles
    }

    fun getStylesByCategory(category: String): List<FrameStyle> {
        val allStyles = FrameStyle.values()
        return when (category) {
            "Classic" -> allStyles.filter { it in classicStyles }
            "Symbol" -> allStyles.filter { it in symbolStyles }
            "Emoji" -> allStyles.filter { it in emojiStyles }
            "♥ Love" -> allStyles.filter { it in heartLoveStyles }
            "🌿 Nature" -> allStyles.filter { it in natureStyles }
            "🌧️ Weather" -> allStyles.filter { it in weatherStyles }
            "💀 Dark" -> allStyles.filter { it in darkStyles }
            "🎉 Festival" -> allStyles.filter { it in festiveStyles }
            "🔥 Power" -> allStyles.filter { it in powerStyles }
            "🎮 Modern" -> allStyles.filter { it in modernStyles }
            "🎈 Fun" -> allStyles.filter { it in funStyles }
            else -> allStyles.toList()
        }
    }

    fun getAllCategories(): List<String> {
        return listOf("Classic", "Symbol", "Emoji", "♥ Love", "🌿 Nature", "🌧️ Weather",
            "💀 Dark", "🎉 Festival", "🔥 Power", "🎮 Modern", "🎈 Fun")
    }
}
