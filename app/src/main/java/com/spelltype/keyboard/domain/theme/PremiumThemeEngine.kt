package com.spelltype.keyboard.domain.theme

import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/**
 * Premium Theme Engine — 12 stunning HD themes with 8K-quality gradients
 * Each theme provides: background, key colors, accent, text, glow, and border
 */
enum class PremiumTheme(
    val displayName: String,
    val emoji: String,
    val isPremium: Boolean = false,
    // Background gradient
    val bgStart: Int,
    val bgCenter: Int,
    val bgEnd: Int,
    // Key colors
    val keyBg: Int,
    val keyBgPressed: Int,
    val keyText: Int,
    // Special keys
    val specialKeyBg: Int,
    val specialKeyText: Int,
    // Accent (Enter, active states)
    val accent: Int,
    val accentPressed: Int,
    // Glow / Neon effect
    val glowColor: Int,
    // Border
    val borderColor: Int,
    val borderAlpha: Int = 80,
    // Toolbar
    val toolbarBg: Int,
    val toolbarText: Int,
    // Suggestions bar
    val suggestionBg: Int,
    val suggestionText: Int,
    val suggestionHighlight: Int,
    // Preview bar
    val previewBg: Int,
    val previewText: Int
) {
    NEON_CYBER(
        displayName = "Neon Cyber",
        emoji = "🌃",
        isPremium = true,
        bgStart = Color.parseColor("#0A0E1A"),
        bgCenter = Color.parseColor("#0D1321"),
        bgEnd = Color.parseColor("#050810"),
        keyBg = Color.parseColor("#141B2D"),
        keyBgPressed = Color.parseColor("#1E3A5F"),
        keyText = Color.parseColor("#E0E7FF"),
        specialKeyBg = Color.parseColor("#0F1729"),
        specialKeyText = Color.parseColor("#94A3B8"),
        accent = Color.parseColor("#00D4FF"),
        accentPressed = Color.parseColor("#00FFE0"),
        glowColor = Color.parseColor("#00D4FF"),
        borderColor = Color.parseColor("#00D4FF"),
        borderAlpha = 60,
        toolbarBg = Color.parseColor("#0B1120"),
        toolbarText = Color.parseColor("#00D4FF"),
        suggestionBg = Color.parseColor("#0B1120"),
        suggestionText = Color.parseColor("#CBD5E1"),
        suggestionHighlight = Color.parseColor("#00D4FF"),
        previewBg = Color.parseColor("#060A14"),
        previewText = Color.parseColor("#00FFE0")
    ),

    AURORA_BOREALIS(
        displayName = "Aurora Borealis",
        emoji = "🌌",
        isPremium = true,
        bgStart = Color.parseColor("#0B1A2B"),
        bgCenter = Color.parseColor("#0F2847"),
        bgEnd = Color.parseColor("#071220"),
        keyBg = Color.parseColor("#132F4C"),
        keyBgPressed = Color.parseColor("#1A4D2E"),
        keyText = Color.parseColor("#E8F5E9"),
        specialKeyBg = Color.parseColor("#0E2440"),
        specialKeyText = Color.parseColor("#81C784"),
        accent = Color.parseColor("#69F0AE"),
        accentPressed = Color.parseColor("#00E676"),
        glowColor = Color.parseColor("#00E676"),
        borderColor = Color.parseColor("#69F0AE"),
        borderAlpha = 50,
        toolbarBg = Color.parseColor("#0A1E35"),
        toolbarText = Color.parseColor("#69F0AE"),
        suggestionBg = Color.parseColor("#0A1E35"),
        suggestionText = Color.parseColor("#A5D6A7"),
        suggestionHighlight = Color.parseColor("#69F0AE"),
        previewBg = Color.parseColor("#061520"),
        previewText = Color.parseColor("#00E676")
    ),

    GALAXY_PURPLE(
        displayName = "Galaxy Purple",
        emoji = "🔮",
        isPremium = true,
        bgStart = Color.parseColor("#12002E"),
        bgCenter = Color.parseColor("#1A0A3E"),
        bgEnd = Color.parseColor("#0A0018"),
        keyBg = Color.parseColor("#1E1145"),
        keyBgPressed = Color.parseColor("#3D1F8C"),
        keyText = Color.parseColor("#EDE7F6"),
        specialKeyBg = Color.parseColor("#150935"),
        specialKeyText = Color.parseColor("#B39DDB"),
        accent = Color.parseColor("#BB86FC"),
        accentPressed = Color.parseColor("#E040FB"),
        glowColor = Color.parseColor("#BB86FC"),
        borderColor = Color.parseColor("#BB86FC"),
        borderAlpha = 55,
        toolbarBg = Color.parseColor("#110830"),
        toolbarText = Color.parseColor("#CE93D8"),
        suggestionBg = Color.parseColor("#110830"),
        suggestionText = Color.parseColor("#D1C4E9"),
        suggestionHighlight = Color.parseColor("#BB86FC"),
        previewBg = Color.parseColor("#0A0020"),
        previewText = Color.parseColor("#E040FB")
    ),

    SUNSET_GOLD(
        displayName = "Sunset Gold",
        emoji = "🌅",
        isPremium = true,
        bgStart = Color.parseColor("#1A0A00"),
        bgCenter = Color.parseColor("#2D1500"),
        bgEnd = Color.parseColor("#120800"),
        keyBg = Color.parseColor("#331A00"),
        keyBgPressed = Color.parseColor("#663300"),
        keyText = Color.parseColor("#FFF3E0"),
        specialKeyBg = Color.parseColor("#261200"),
        specialKeyText = Color.parseColor("#FFB74D"),
        accent = Color.parseColor("#FF9800"),
        accentPressed = Color.parseColor("#FFD600"),
        glowColor = Color.parseColor("#FF9800"),
        borderColor = Color.parseColor("#FF9800"),
        borderAlpha = 50,
        toolbarBg = Color.parseColor("#1A0D00"),
        toolbarText = Color.parseColor("#FFB74D"),
        suggestionBg = Color.parseColor("#1A0D00"),
        suggestionText = Color.parseColor("#FFE0B2"),
        suggestionHighlight = Color.parseColor("#FF9800"),
        previewBg = Color.parseColor("#120800"),
        previewText = Color.parseColor("#FFD600")
    ),

    OCEAN_DEEP(
        displayName = "Ocean Deep",
        emoji = "🌊",
        bgStart = Color.parseColor("#001219"),
        bgCenter = Color.parseColor("#001F3F"),
        bgEnd = Color.parseColor("#000A14"),
        keyBg = Color.parseColor("#0A2540"),
        keyBgPressed = Color.parseColor("#1B4965"),
        keyText = Color.parseColor("#CAF0F8"),
        specialKeyBg = Color.parseColor("#061C30"),
        specialKeyText = Color.parseColor("#48CAE4"),
        accent = Color.parseColor("#0077B6"),
        accentPressed = Color.parseColor("#00B4D8"),
        glowColor = Color.parseColor("#48CAE4"),
        borderColor = Color.parseColor("#48CAE4"),
        borderAlpha = 45,
        toolbarBg = Color.parseColor("#001525"),
        toolbarText = Color.parseColor("#48CAE4"),
        suggestionBg = Color.parseColor("#001525"),
        suggestionText = Color.parseColor("#90E0EF"),
        suggestionHighlight = Color.parseColor("#0077B6"),
        previewBg = Color.parseColor("#000A14"),
        previewText = Color.parseColor("#00B4D8")
    ),

    FOREST_EMERALD(
        displayName = "Forest Emerald",
        emoji = "🌲",
        bgStart = Color.parseColor("#001A0D"),
        bgCenter = Color.parseColor("#002E15"),
        bgEnd = Color.parseColor("#000F07"),
        keyBg = Color.parseColor("#0A3D20"),
        keyBgPressed = Color.parseColor("#1B6B3A"),
        keyText = Color.parseColor("#E8F5E9"),
        specialKeyBg = Color.parseColor("#062E17"),
        specialKeyText = Color.parseColor("#66BB6A"),
        accent = Color.parseColor("#2E7D32"),
        accentPressed = Color.parseColor("#43A047"),
        glowColor = Color.parseColor("#66BB6A"),
        borderColor = Color.parseColor("#66BB6A"),
        borderAlpha = 45,
        toolbarBg = Color.parseColor("#001A0D"),
        toolbarText = Color.parseColor("#81C784"),
        suggestionBg = Color.parseColor("#001A0D"),
        suggestionText = Color.parseColor("#A5D6A7"),
        suggestionHighlight = Color.parseColor("#2E7D32"),
        previewBg = Color.parseColor("#000F07"),
        previewText = Color.parseColor("#43A047")
    ),

    ROYAL_CRIMSON(
        displayName = "Royal Crimson",
        emoji = "👑",
        isPremium = true,
        bgStart = Color.parseColor("#1A0005"),
        bgCenter = Color.parseColor("#2E000A"),
        bgEnd = Color.parseColor("#100003"),
        keyBg = Color.parseColor("#3D0A15"),
        keyBgPressed = Color.parseColor("#6B1A2E"),
        keyText = Color.parseColor("#FFEBEE"),
        specialKeyBg = Color.parseColor("#2A0810"),
        specialKeyText = Color.parseColor("#EF9A9A"),
        accent = Color.parseColor("#C62828"),
        accentPressed = Color.parseColor("#EF5350"),
        glowColor = Color.parseColor("#FF1744"),
        borderColor = Color.parseColor("#EF5350"),
        borderAlpha = 55,
        toolbarBg = Color.parseColor("#1A0508"),
        toolbarText = Color.parseColor("#EF9A9A"),
        suggestionBg = Color.parseColor("#1A0508"),
        suggestionText = Color.parseColor("#FFCDD2"),
        suggestionHighlight = Color.parseColor("#C62828"),
        previewBg = Color.parseColor("#100003"),
        previewText = Color.parseColor("#EF5350")
    ),

    ARCTIC_ICE(
        displayName = "Arctic Ice",
        emoji = "❄️",
        isPremium = true,
        bgStart = Color.parseColor("#E8F0FE"),
        bgCenter = Color.parseColor("#F0F4FF"),
        bgEnd = Color.parseColor("#DCE8FF"),
        keyBg = Color.parseColor("#FFFFFF"),
        keyBgPressed = Color.parseColor("#BBDEFB"),
        keyText = Color.parseColor("#1A237E"),
        specialKeyBg = Color.parseColor("#E3F2FD"),
        specialKeyText = Color.parseColor("#42A5F5"),
        accent = Color.parseColor("#1E88E5"),
        accentPressed = Color.parseColor("#42A5F5"),
        glowColor = Color.parseColor("#64B5F6"),
        borderColor = Color.parseColor("#90CAF9"),
        borderAlpha = 120,
        toolbarBg = Color.parseColor("#E3F2FD"),
        toolbarText = Color.parseColor("#1565C0"),
        suggestionBg = Color.parseColor("#E8EAF6"),
        suggestionText = Color.parseColor("#283593"),
        suggestionHighlight = Color.parseColor("#1E88E5"),
        previewBg = Color.parseColor("#DCE8FF"),
        previewText = Color.parseColor("#1565C0")
    ),

    VOLCANIC_FIRE(
        displayName = "Volcanic Fire",
        emoji = "🌋",
        isPremium = true,
        bgStart = Color.parseColor("#1A0000"),
        bgCenter = Color.parseColor("#330000"),
        bgEnd = Color.parseColor("#0D0000"),
        keyBg = Color.parseColor("#4A0E0E"),
        keyBgPressed = Color.parseColor("#8B0000"),
        keyText = Color.parseColor("#FFEBEE"),
        specialKeyBg = Color.parseColor("#2E0505"),
        specialKeyText = Color.parseColor("#FF6F00"),
        accent = Color.parseColor("#FF6D00"),
        accentPressed = Color.parseColor("#FFAB00"),
        glowColor = Color.parseColor("#FF6D00"),
        borderColor = Color.parseColor("#FF6D00"),
        borderAlpha = 60,
        toolbarBg = Color.parseColor("#1A0505"),
        toolbarText = Color.parseColor("#FF6D00"),
        suggestionBg = Color.parseColor("#1A0505"),
        suggestionText = Color.parseColor("#FFCC80"),
        suggestionHighlight = Color.parseColor("#FF6D00"),
        previewBg = Color.parseColor("#0D0000"),
        previewText = Color.parseColor("#FFAB00")
    ),

    SAKURA_PINK(
        displayName = "Sakura Pink",
        emoji = "🌸",
        isPremium = true,
        bgStart = Color.parseColor("#1A0A15"),
        bgCenter = Color.parseColor("#2E1028"),
        bgEnd = Color.parseColor("#100810"),
        keyBg = Color.parseColor("#3D1535"),
        keyBgPressed = Color.parseColor("#6B2860"),
        keyText = Color.parseColor("#FCE4EC"),
        specialKeyBg = Color.parseColor("#2A0E22"),
        specialKeyText = Color.parseColor("#F48FB1"),
        accent = Color.parseColor("#EC407A"),
        accentPressed = Color.parseColor("#F06292"),
        glowColor = Color.parseColor("#FF4081"),
        borderColor = Color.parseColor("#F48FB1"),
        borderAlpha = 55,
        toolbarBg = Color.parseColor("#1A0A15"),
        toolbarText = Color.parseColor("#F48FB1"),
        suggestionBg = Color.parseColor("#1A0A15"),
        suggestionText = Color.parseColor("#F8BBD0"),
        suggestionHighlight = Color.parseColor("#EC407A"),
        previewBg = Color.parseColor("#100810"),
        previewText = Color.parseColor("#F06292")
    ),

    MIDNIGHT_SAPPHIRE(
        displayName = "Midnight Sapphire",
        emoji = "💎",
        isPremium = true,
        bgStart = Color.parseColor("#000A1A"),
        bgCenter = Color.parseColor("#001035"),
        bgEnd = Color.parseColor("#000510"),
        keyBg = Color.parseColor("#0A1E45"),
        keyBgPressed = Color.parseColor("#1A3A6B"),
        keyText = Color.parseColor("#E3F2FD"),
        specialKeyBg = Color.parseColor("#071530"),
        specialKeyText = Color.parseColor("#5C6BC0"),
        accent = Color.parseColor("#303F9F"),
        accentPressed = Color.parseColor("#3F51B5"),
        glowColor = Color.parseColor("#536DFE"),
        borderColor = Color.parseColor("#536DFE"),
        borderAlpha = 50,
        toolbarBg = Color.parseColor("#000A1A"),
        toolbarText = Color.parseColor("#7986CB"),
        suggestionBg = Color.parseColor("#000A1A"),
        suggestionText = Color.parseColor("#C5CAE9"),
        suggestionHighlight = Color.parseColor("#303F9F"),
        previewBg = Color.parseColor("#000510"),
        previewText = Color.parseColor("#536DFE")
    ),

    DIAMOND_PLATINUM(
        displayName = "Diamond Platinum",
        emoji = "💠",
        isPremium = true,
        bgStart = Color.parseColor("#F5F5F5"),
        bgCenter = Color.parseColor("#FAFAFA"),
        bgEnd = Color.parseColor("#EEEEEE"),
        keyBg = Color.parseColor("#FFFFFF"),
        keyBgPressed = Color.parseColor("#E0E0E0"),
        keyText = Color.parseColor("#212121"),
        specialKeyBg = Color.parseColor("#F0F0F0"),
        specialKeyText = Color.parseColor("#757575"),
        accent = Color.parseColor("#424242"),
        accentPressed = Color.parseColor("#616161"),
        glowColor = Color.parseColor("#BDBDBD"),
        borderColor = Color.parseColor("#BDBDBD"),
        borderAlpha = 150,
        toolbarBg = Color.parseColor("#ECEFF1"),
        toolbarText = Color.parseColor("#37474F"),
        suggestionBg = Color.parseColor("#ECEFF1"),
        suggestionText = Color.parseColor("#455A64"),
        suggestionHighlight = Color.parseColor("#424242"),
        previewBg = Color.parseColor("#E0E0E0"),
        previewText = Color.parseColor("#212121")
    );

    /** Create gradient background drawable */
    fun createBackgroundDrawable(orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM): GradientDrawable {
        return GradientDrawable(orientation, intArrayOf(bgStart, bgCenter, bgEnd))
    }

    /** Create key background with rounded corners */
    fun createKeyBackground(cornerRadiusPx: Float, pressed: Boolean = false): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusPx
            setColor(if (pressed) keyBgPressed else keyBg)
            setStroke(1, Color.argb(borderAlpha, Color.red(borderColor), Color.green(borderColor), Color.blue(borderColor)))
        }
    }

    /** Create accent key background */
    fun createAccentBackground(cornerRadiusPx: Float, pressed: Boolean = false): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusPx
            setColor(if (pressed) accentPressed else accent)
        }
    }
}

object PremiumThemeEngine {
    fun getAllThemes(): List<PremiumTheme> = PremiumTheme.values().toList()
    fun getFreeThemes(): List<PremiumTheme> = PremiumTheme.values().filter { !it.isPremium }
    fun getPremiumThemes(): List<PremiumTheme> = PremiumTheme.values().filter { it.isPremium }
    fun getThemeByName(name: String): PremiumTheme? = PremiumTheme.values().find { it.name == name }
}
