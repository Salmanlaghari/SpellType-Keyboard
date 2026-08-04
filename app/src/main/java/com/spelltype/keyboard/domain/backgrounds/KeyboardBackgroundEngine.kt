package com.spelltype.keyboard.domain.backgrounds

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable

/**
 * 20+ Keyboard Background Engine
 * Real drawable backgrounds: Flowers, Cherry Blossom, Stars, Nebula, etc.
 */
object KeyboardBackgroundEngine {

    data class KeyboardBackground(
        val id: String,
        val name: String,
        val emoji: String,
        val type: BackgroundType,
        val colors: IntArray,
        val patternAlpha: Int = 30
    )

    enum class BackgroundType {
        GRADIENT, PATTERN_DOTS, PATTERN_LINES, PATTERN_GRID,
        PATTERN_WAVES, PATTERN_DIAMONDS, PATTERN_STARS,
        PATTERN_HEARTS, PATTERN_FLOWERS, PATTERN_CHERRY,
        PATTERN_GALAXY, PATTERN_MARBLE, PATTERN_CIRCUIT,
        PATTERN_SNOW, PATTERN_FIRE, PATTERN_OCEAN,
        PATTERN_FOREST, PATTERN_SAKURA, PATTERN_BUTTERFLY,
        PATTERN_FEATHER, PATTERN_MOSAIC, PATTERN_ZIGZAG
    }

    val ALL = listOf(
        KeyboardBackground("flowers", "Flowers", "🌺", BackgroundType.PATTERN_FLOWERS,
            intArrayOf(Color.parseColor("#1A0A15"), Color.parseColor("#2D1028")), 40),
        KeyboardBackground("cherry_blossom", "Cherry Blossom", "🌸", BackgroundType.PATTERN_CHERRY,
            intArrayOf(Color.parseColor("#2D0A1E"), Color.parseColor("#4A1030")), 35),
        KeyboardBackground("sakura", "Sakura Garden", "🌸", BackgroundType.PATTERN_SAKURA,
            intArrayOf(Color.parseColor("#1A0F18"), Color.parseColor("#301530")), 30),
        KeyboardBackground("starfield", "Starfield", "⭐", BackgroundType.PATTERN_STARS,
            intArrayOf(Color.parseColor("#050510"), Color.parseColor("#0A0A20")), 25),
        KeyboardBackground("galaxy", "Galaxy Nebula", "🌌", BackgroundType.PATTERN_GALAXY,
            intArrayOf(Color.parseColor("#0D001A"), Color.parseColor("#1A0033")), 20),
        KeyboardBackground("ocean_waves", "Ocean Waves", "🌊", BackgroundType.PATTERN_OCEAN,
            intArrayOf(Color.parseColor("#001219"), Color.parseColor("#001F3F")), 30),
        KeyboardBackground("forest", "Enchanted Forest", "🌲", BackgroundType.PATTERN_FOREST,
            intArrayOf(Color.parseColor("#001A0D"), Color.parseColor("#002E15")), 25),
        KeyboardBackground("snow", "Winter Snow", "❄️", BackgroundType.PATTERN_SNOW,
            intArrayOf(Color.parseColor("#0A0F1E"), Color.parseColor("#111B33")), 35),
        KeyboardBackground("fire", "Eternal Flame", "🔥", BackgroundType.PATTERN_FIRE,
            intArrayOf(Color.parseColor("#1A0000"), Color.parseColor("#330000")), 30),
        KeyboardBackground("butterfly", "Butterfly Garden", "🦋", BackgroundType.PATTERN_BUTTERFLY,
            intArrayOf(Color.parseColor("#0A1A2B"), Color.parseColor("#0F2847")), 30),
        KeyboardBackground("marble", "Royal Marble", "🏛️", BackgroundType.PATTERN_MARBLE,
            intArrayOf(Color.parseColor("#F5F5F5"), Color.parseColor("#E0E0E0")), 15),
        KeyboardBackground("circuit", "Cyber Circuit", "🤖", BackgroundType.PATTERN_CIRCUIT,
            intArrayOf(Color.parseColor("#050510"), Color.parseColor("#0A0A20")), 25),
        KeyboardBackground("dots", "Polka Dots", "⚬", BackgroundType.PATTERN_DOTS,
            intArrayOf(Color.parseColor("#1A0A15"), Color.parseColor("#2D1028")), 40),
        KeyboardBackground("lines", "Clean Lines", "══", BackgroundType.PATTERN_LINES,
            intArrayOf(Color.parseColor("#0B0F19"), Color.parseColor("#0D1321")), 20),
        KeyboardBackground("grid", "Tech Grid", "▦", BackgroundType.PATTERN_GRID,
            intArrayOf(Color.parseColor("#050510"), Color.parseColor("#0A0A20")), 25),
        KeyboardBackground("waves", "Gentle Waves", "〰", BackgroundType.PATTERN_WAVES,
            intArrayOf(Color.parseColor("#001219"), Color.parseColor("#001F3F")), 30),
        KeyboardBackground("diamonds", "Diamond Pattern", "◆", BackgroundType.PATTERN_DIAMONDS,
            intArrayOf(Color.parseColor("#12002E"), Color.parseColor("#1A0A3E")), 25),
        KeyboardBackground("hearts", "Love Hearts", "♥", BackgroundType.PATTERN_HEARTS,
            intArrayOf(Color.parseColor("#1A0005"), Color.parseColor("#2E000A")), 35),
        KeyboardBackground("feather", "Feather Dreams", "🪶", BackgroundType.PATTERN_FEATHER,
            intArrayOf(Color.parseColor("#0A1A2B"), Color.parseColor("#0F2847")), 25),
        KeyboardBackground("mosaic", "Mosaic Art", "▣", BackgroundType.PATTERN_MOSAIC,
            intArrayOf(Color.parseColor("#0D001A"), Color.parseColor("#1A0033")), 30),
        KeyboardBackground("zigzag", "Zigzag Energy", "↯", BackgroundType.PATTERN_ZIGZAG,
            intArrayOf(Color.parseColor("#1A0A00"), Color.parseColor("#2D1500")), 25),
        KeyboardBackground("aurora", "Aurora Sky", "🌌", BackgroundType.GRADIENT,
            intArrayOf(Color.parseColor("#0B1A2B"), Color.parseColor("#0F2847"), Color.parseColor("#071220")), 20),
        KeyboardBackground("sunset", "Golden Sunset", "🌅", BackgroundType.GRADIENT,
            intArrayOf(Color.parseColor("#1A0A00"), Color.parseColor("#2D1500"), Color.parseColor("#120800")), 20),
        KeyboardBackground("midnight", "Midnight Blue", "🌙", BackgroundType.GRADIENT,
            intArrayOf(Color.parseColor("#000A1A"), Color.parseColor("#001035"), Color.parseColor("#000510")), 20)
    )

    /**
     * Create the background drawable
     */
    fun createBackground(bg: KeyboardBackground): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TL_BR, bg.colors).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
            cornerRadius = 0f
        }
    }

    /**
     * Create pattern overlay paint
     */
    fun createPatternPaint(bg: KeyboardBackground, width: Int, height: Int): Paint? {
        return when (bg.type) {
            BackgroundType.PATTERN_DOTS -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = bg.patternAlpha
                style = Paint.Style.FILL
            }
            BackgroundType.PATTERN_LINES -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = bg.patternAlpha
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            BackgroundType.PATTERN_HEARTS -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FF69B4")
                alpha = bg.patternAlpha
                textSize = 24f
            }
            BackgroundType.PATTERN_FLOWERS -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFB6C1")
                alpha = bg.patternAlpha
                textSize = 20f
            }
            BackgroundType.PATTERN_STARS -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFD700")
                alpha = bg.patternAlpha
                textSize = 16f
            }
            BackgroundType.PATTERN_SNOW -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = bg.patternAlpha + 20
                textSize = 18f
            }
            BackgroundType.PATTERN_CHERRY -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFB7C5")
                alpha = bg.patternAlpha
                textSize = 22f
            }
            BackgroundType.PATTERN_SAKURA -> Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFB7C5")
                alpha = bg.patternAlpha
                textSize = 18f
            }
            else -> null
        }
    }

    fun getAllBackgrounds() = ALL
    fun getBackgroundById(id: String) = ALL.find { it.id == id }
}
