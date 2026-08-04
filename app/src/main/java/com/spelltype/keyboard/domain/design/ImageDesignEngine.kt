package com.spelltype.keyboard.domain.design

import android.graphics.Color

/**
 * Image Custom Design Engine — AI-powered keyboard background customization
 * Allows users to create unique keyboard designs with AI suggestions
 */
object ImageDesignEngine {

    data class GradientPreset(
        val name: String,
        val emoji: String,
        val colors: List<Int>,
        val isPremium: Boolean = false
    )

    data class PatternPreset(
        val name: String,
        val emoji: String,
        val type: PatternType,
        val isPremium: Boolean = false
    )

    enum class PatternType {
        DOTS, LINES, GRID, WAVES, DIAMONDS, HEXAGONS, STARS, HEARTS,
        ZIGZAG, CHEVRON, MOSAIC, CIRCUIT, GALAXY, MARBLE, WOOD, METAL
    }

    enum class BlurLevel(val displayName: String) {
        NONE("None"),
        LIGHT("Light"),
        MEDIUM("Medium"),
        HEAVY("Heavy"),
        GLASS("Glass")
    }

    enum class OverlayStyle(val displayName: String) {
        NONE("None"),
        VIGNETTE("Vignette"),
        NOISE("Noise"),
        GRAIN("Film Grain"),
        GLASS("Glassmorphism"),
        GRADIENT("Gradient Overlay"),
        DARK("Dark Overlay"),
        LIGHT("Light Overlay")
    }

    // Premium gradient presets
    val gradientPresets = listOf(
        GradientPreset("Sunset Blaze", "🌅", listOf(
            Color.parseColor("#FF6B35"), Color.parseColor("#F7931E"), Color.parseColor("#FFD700")
        )),
        GradientPreset("Ocean Breeze", "🌊", listOf(
            Color.parseColor("#0077B6"), Color.parseColor("#00B4D8"), Color.parseColor("#90E0EF")
        )),
        GradientPreset("Forest Mist", "🌲", listOf(
            Color.parseColor("#2D6A4F"), Color.parseColor("#40916C"), Color.parseColor("#95D5B2")
        )),
        GradientPreset("Royal Purple", "👑", listOf(
            Color.parseColor("#5A189A"), Color.parseColor("#7B2CBF"), Color.parseColor("#C77DFF")
        ), isPremium = true),
        GradientPreset("Cherry Blossom", "🌸", listOf(
            Color.parseColor("#FF6B9D"), Color.parseColor("#C06C84"), Color.parseColor("#F67280")
        ), isPremium = true),
        GradientPreset("Midnight Blue", "🌙", listOf(
            Color.parseColor("#0D1B2A"), Color.parseColor("#1B263B"), Color.parseColor("#415A77")
        )),
        GradientPreset("Neon Glow", "💡", listOf(
            Color.parseColor("#00F5FF"), Color.parseColor("#00D4FF"), Color.parseColor("#0099FF")
        ), isPremium = true),
        GradientPreset("Golden Hour", "☀️", listOf(
            Color.parseColor("#FF9A00"), Color.parseColor("#FF6F00"), Color.parseColor("#E65100")
        )),
        GradientPreset("Arctic Aurora", "🌌", listOf(
            Color.parseColor("#00C9FF"), Color.parseColor("#92FE9D"), Color.parseColor("#00C9FF")
        ), isPremium = true),
        GradientPreset("Rose Gold", "💎", listOf(
            Color.parseColor("#B76E79"), Color.parseColor("#E8B4BC"), Color.parseColor("#F5C6C6")
        ), isPremium = true),
        GradientPreset("Deep Space", "🚀", listOf(
            Color.parseColor("#0F0C29"), Color.parseColor("#302B63"), Color.parseColor("#24243E")
        )),
        GradientPreset("Lava Flow", "🌋", listOf(
            Color.parseColor("#FF0000"), Color.parseColor("#FF6600"), Color.parseColor("#FFCC00")
        ), isPremium = true)
    )

    // Pattern presets
    val patternPresets = listOf(
        PatternPreset("Dots", "⚬", PatternType.DOTS),
        PatternPreset("Lines", "══", PatternType.LINES),
        PatternPreset("Grid", "▦", PatternType.GRID),
        PatternPreset("Waves", "〰", PatternType.WAVES),
        PatternPreset("Diamonds", "◆", PatternType.DIAMONDS),
        PatternPreset("Hexagons", "⬡", PatternType.HEXAGONS, isPremium = true),
        PatternPreset("Stars", "★", PatternType.STARS, isPremium = true),
        PatternPreset("Hearts", "♥", PatternType.HEARTS, isPremium = true),
        PatternPreset("Zigzag", "↯", PatternType.ZIGZAG),
        PatternPreset("Chevron", "❯", PatternType.CHEVRON),
        PatternPreset("Mosaic", "▣", PatternType.MOSAIC, isPremium = true),
        PatternPreset("Circuit", "⊞", PatternType.CIRCUIT, isPremium = true),
        PatternPreset("Galaxy", "✧", PatternType.GALAXY, isPremium = true),
        PatternPreset("Marble", "◎", PatternType.MARBLE, isPremium = true),
        PatternPreset("Wood", "≡", PatternType.WOOD),
        PatternPreset("Metal", "⬜", PatternType.METAL, isPremium = true)
    )

    /**
     * AI-suggested design based on user preferences
     */
    fun getAIDesignSuggestion(
        favoriteColor: String? = null,
        mood: String? = null,
        timeOfDay: String? = null
    ): DesignSuggestion {
        val suggestedGradient = when {
            mood == "energetic" -> gradientPresets.find { it.name == "Neon Glow" }
            mood == "calm" -> gradientPresets.find { it.name == "Ocean Breeze" }
            mood == "romantic" -> gradientPresets.find { it.name == "Cherry Blossom" }
            timeOfDay == "morning" -> gradientPresets.find { it.name == "Golden Hour" }
            timeOfDay == "night" -> gradientPresets.find { it.name == "Midnight Blue" }
            favoriteColor == "blue" -> gradientPresets.find { it.name == "Ocean Breeze" }
            favoriteColor == "red" -> gradientPresets.find { it.name == "Lava Flow" }
            favoriteColor == "green" -> gradientPresets.find { it.name == "Forest Mist" }
            favoriteColor == "purple" -> gradientPresets.find { it.name == "Royal Purple" }
            else -> gradientPresets.random()
        }

        val suggestedPattern = patternPresets.filter { !it.isPremium }.random()
        val suggestedBlur = BlurLevel.MEDIUM
        val suggestedOverlay = OverlayStyle.GLASS

        return DesignSuggestion(
            gradient = suggestedGradient,
            pattern = suggestedPattern,
            blur = suggestedBlur,
            overlay = suggestedOverlay,
            opacity = 70,
            description = "A ${suggestedGradient?.name ?: "custom"} design with ${suggestedPattern.name.lowercase()} pattern"
        )
    }

    data class DesignSuggestion(
        val gradient: GradientPreset?,
        val pattern: PatternPreset?,
        val blur: BlurLevel,
        val overlay: OverlayStyle,
        val opacity: Int,
        val description: String
    )

    fun getAllGradients(): List<GradientPreset> = gradientPresets
    fun getAllPatterns(): List<PatternPreset> = patternPresets
    fun getBlurLevels(): List<BlurLevel> = BlurLevel.values().toList()
    fun getOverlayStyles(): List<OverlayStyle> = OverlayStyle.values().toList()
}
