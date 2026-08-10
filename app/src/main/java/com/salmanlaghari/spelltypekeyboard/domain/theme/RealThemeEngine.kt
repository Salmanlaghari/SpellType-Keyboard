package com.salmanlaghari.spelltypekeyboard.domain.theme

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

/**
 * Real Premium Theme Engine — 44+ HD 8K Animated & 3D Themes
 * Each theme has: animated gradient background, glow keys, particle effects, 3D keycaps
 */

data class RealTheme(
    val id: String,
    val name: String,
    val emoji: String,
    val bgColors: IntArray,
    val keyColor: Int,
    val keyPressedColor: Int,
    val keyTextColor: Int,
    val accentColor: Int,
    val glowColor: Int,
    val borderColor: Int,
    val toolbarBg: Int,
    val toolbarText: Int,
    val suggestionBg: Int,
    val previewBg: Int,
    val isLight: Boolean = false,
    val isAnimated: Boolean = true,
    val particleColor: Int = Color.WHITE,
    val is3DTheme: Boolean = false
) {
    companion object {
        var force3D: Boolean = true
        val ALL = listOf(
            // --- 11 SPECTACULAR NEW 3D UI THEMES (3D KEYCAPS) ---
            RealTheme("3d_glass_neon", "3D Glass Neon", "🌃",
                intArrayOf(Color.parseColor("#060A17"), Color.parseColor("#0C152B"), Color.parseColor("#03050C")),
                Color.parseColor("#15203D"), Color.parseColor("#22396B"), Color.parseColor("#00FFE0"),
                Color.parseColor("#00D4FF"), Color.parseColor("#00FFE0"), Color.parseColor("#004C80"),
                Color.parseColor("#080D1D"), Color.parseColor("#00FFE0"), Color.parseColor("#080D1D"),
                Color.parseColor("#03050D"), is3DTheme = true, particleColor = Color.parseColor("#00FFE0")),

            RealTheme("3d_glossy_ruby", "3D Glossy Ruby", "🔴",
                intArrayOf(Color.parseColor("#1E0005"), Color.parseColor("#3B000C"), Color.parseColor("#100003")),
                Color.parseColor("#660D1E"), Color.parseColor("#991A32"), Color.parseColor("#FFE5EB"),
                Color.parseColor("#FF1E46"), Color.parseColor("#FF1E46"), Color.parseColor("#4A020F"),
                Color.parseColor("#1F0106"), Color.parseColor("#FFE5EB"), Color.parseColor("#1F0106"),
                Color.parseColor("#100003"), is3DTheme = true, particleColor = Color.parseColor("#FF1E46")),

            RealTheme("3d_embossed_gold", "3D Embossed Gold", "🏆",
                intArrayOf(Color.parseColor("#1A1100"), Color.parseColor("#332200"), Color.parseColor("#0D0800")),
                Color.parseColor("#5C3E00"), Color.parseColor("#8F6200"), Color.parseColor("#FFE0B2"),
                Color.parseColor("#FFC107"), Color.parseColor("#FFD54F"), Color.parseColor("#331A00"),
                Color.parseColor("#1C1300"), Color.parseColor("#FFE0B2"), Color.parseColor("#1C1300"),
                Color.parseColor("#0D0800"), is3DTheme = true, particleColor = Color.parseColor("#FFC107")),

            RealTheme("3d_stealth_carbon", "3D Stealth Carbon", "⚫",
                intArrayOf(Color.parseColor("#121212"), Color.parseColor("#1E1E1E"), Color.parseColor("#0A0A0A")),
                Color.parseColor("#262626"), Color.parseColor("#404040"), Color.parseColor("#F5F5F5"),
                Color.parseColor("#A3A3A3"), Color.parseColor("#E5E5E5"), Color.parseColor("#171717"),
                Color.parseColor("#121212"), Color.parseColor("#E5E5E5"), Color.parseColor("#121212"),
                Color.parseColor("#0A0A0A"), is3DTheme = true, particleColor = Color.parseColor("#A3A3A3")),

            RealTheme("3d_clay_soft", "3D Soft Clay", "🧸",
                intArrayOf(Color.parseColor("#F1F5F9"), Color.parseColor("#E2E8F0"), Color.parseColor("#CBD5E1")),
                Color.parseColor("#FFFFFF"), Color.parseColor("#F1F5F9"), Color.parseColor("#334155"),
                Color.parseColor("#3B82F6"), Color.parseColor("#3B82F6"), Color.parseColor("#94A3B8"),
                Color.parseColor("#E2E8F0"), Color.parseColor("#334155"), Color.parseColor("#E2E8F0"),
                Color.parseColor("#CBD5E1"), isLight = true, is3DTheme = true, particleColor = Color.parseColor("#3B82F6")),

            RealTheme("3d_chrome_metal", "3D Chrome Metal", "🥈",
                intArrayOf(Color.parseColor("#1E293B"), Color.parseColor("#334155"), Color.parseColor("#0F172A")),
                Color.parseColor("#475569"), Color.parseColor("#64748B"), Color.parseColor("#F8FAFC"),
                Color.parseColor("#38BDF8"), Color.parseColor("#38BDF8"), Color.parseColor("#1E293B"),
                Color.parseColor("#0F172A"), Color.parseColor("#F8FAFC"), Color.parseColor("#0F172A"),
                Color.parseColor("#0F172A"), is3DTheme = true, particleColor = Color.parseColor("#38BDF8")),

            RealTheme("3d_hologram", "3D Hologram", "🦄",
                intArrayOf(Color.parseColor("#1E002A"), Color.parseColor("#3D0053"), Color.parseColor("#0F0015")),
                Color.parseColor("#5A0D7A"), Color.parseColor("#8015AD"), Color.parseColor("#FDF2FF"),
                Color.parseColor("#FF00FF"), Color.parseColor("#00FFFF"), Color.parseColor("#3B0053"),
                Color.parseColor("#0F0015"), Color.parseColor("#FDF2FF"), Color.parseColor("#0F0015"),
                Color.parseColor("#0F0015"), is3DTheme = true, particleColor = Color.parseColor("#FF00FF")),

            RealTheme("3d_bubble_gum", "3D Bubble Gum", "🍬",
                intArrayOf(Color.parseColor("#2E081A"), Color.parseColor("#4C0D2C"), Color.parseColor("#1A0410")),
                Color.parseColor("#7A1448"), Color.parseColor("#AD1D67"), Color.parseColor("#FFF0F6"),
                Color.parseColor("#FF4DAD"), Color.parseColor("#FF82C7"), Color.parseColor("#400623"),
                Color.parseColor("#1A0410"), Color.parseColor("#FFF0F6"), Color.parseColor("#1A0410"),
                Color.parseColor("#1A0410"), is3DTheme = true, particleColor = Color.parseColor("#FF4DAD")),

            RealTheme("3d_retro_key", "3D Retro Keycap", "⌨️",
                intArrayOf(Color.parseColor("#2D2B2A"), Color.parseColor("#3E3C3A"), Color.parseColor("#1D1C1B")),
                Color.parseColor("#E6DED8"), Color.parseColor("#FFFFFF"), Color.parseColor("#1D1C1B"),
                Color.parseColor("#C43E2B"), Color.parseColor("#C43E2B"), Color.parseColor("#8F8884"),
                Color.parseColor("#1D1C1B"), Color.parseColor("#1D1C1B"), Color.parseColor("#1D1C1B"),
                Color.parseColor("#1D1C1B"), is3DTheme = true, particleColor = Color.parseColor("#C43E2B")),

            RealTheme("3d_velvet_purple", "3D Velvet Purple", "🔮",
                intArrayOf(Color.parseColor("#12002E"), Color.parseColor("#25005E"), Color.parseColor("#0A001A")),
                Color.parseColor("#410A8F"), Color.parseColor("#5C10C4"), Color.parseColor("#F7F0FF"),
                Color.parseColor("#D485FF"), Color.parseColor("#CE93D8"), Color.parseColor("#25005E"),
                Color.parseColor("#0A001A"), Color.parseColor("#F7F0FF"), Color.parseColor("#0A001A"),
                Color.parseColor("#0A001A"), is3DTheme = true, particleColor = Color.parseColor("#D485FF")),

            RealTheme("3d_cyber_orange", "3D Cyber Orange", "☣️",
                intArrayOf(Color.parseColor("#1A0F00"), Color.parseColor("#331E00"), Color.parseColor("#0D0800")),
                Color.parseColor("#663D00"), Color.parseColor("#995C00"), Color.parseColor("#FFF3E0"),
                Color.parseColor("#FF9100"), Color.parseColor("#FF9100"), Color.parseColor("#4D2E00"),
                Color.parseColor("#0D0800"), Color.parseColor("#FFF3E0"), Color.parseColor("#0D0800"),
                Color.parseColor("#0D0800"), is3DTheme = true, particleColor = Color.parseColor("#FF9100")),

            // --- ORIGINAL REAL THEMES ---
            // 1. Neon Cyber
            RealTheme("neon_cyber", "Neon Cyber", "🌃",
                intArrayOf(Color.parseColor("#0A0E1A"), Color.parseColor("#0D1321"), Color.parseColor("#050810")),
                Color.parseColor("#141B2D"), Color.parseColor("#1E3A5F"), Color.parseColor("#E0E7FF"),
                Color.parseColor("#00D4FF"), Color.parseColor("#00D4FF"), Color.parseColor("#00D4FF"),
                Color.parseColor("#0B1120"), Color.parseColor("#00D4FF"), Color.parseColor("#0B1120"),
                Color.parseColor("#060A14"), particleColor = Color.parseColor("#00D4FF")),
            // 2. Aurora Borealis
            RealTheme("aurora", "Aurora Borealis", "🌌",
                intArrayOf(Color.parseColor("#0B1A2B"), Color.parseColor("#0F2847"), Color.parseColor("#071220")),
                Color.parseColor("#132F4C"), Color.parseColor("#1A4D2E"), Color.parseColor("#E8F5E9"),
                Color.parseColor("#69F0AE"), Color.parseColor("#00E676"), Color.parseColor("#69F0AE"),
                Color.parseColor("#0A1E35"), Color.parseColor("#69F0AE"), Color.parseColor("#0A1E35"),
                Color.parseColor("#061520"), particleColor = Color.parseColor("#69F0AE")),
            // 3. Galaxy Purple
            RealTheme("galaxy", "Galaxy Purple", "🔮",
                intArrayOf(Color.parseColor("#12002E"), Color.parseColor("#1A0A3E"), Color.parseColor("#0A0018")),
                Color.parseColor("#1E1145"), Color.parseColor("#3D1F8C"), Color.parseColor("#EDE7F6"),
                Color.parseColor("#BB86FC"), Color.parseColor("#E040FB"), Color.parseColor("#BB86FC"),
                Color.parseColor("#110830"), Color.parseColor("#CE93D8"), Color.parseColor("#110830"),
                Color.parseColor("#0A0020"), particleColor = Color.parseColor("#E040FB")),
            // 4. Sunset Gold
            RealTheme("sunset", "Sunset Gold", "🌅",
                intArrayOf(Color.parseColor("#1A0A00"), Color.parseColor("#2D1500"), Color.parseColor("#120800")),
                Color.parseColor("#331A00"), Color.parseColor("#663300"), Color.parseColor("#FFF3E0"),
                Color.parseColor("#FF9800"), Color.parseColor("#FFD600"), Color.parseColor("#FF9800"),
                Color.parseColor("#1A0D00"), Color.parseColor("#FFB74D"), Color.parseColor("#1A0D00"),
                Color.parseColor("#120800"), particleColor = Color.parseColor("#FFD600")),
            // 5. Ocean Deep
            RealTheme("ocean", "Ocean Deep", "🌊",
                intArrayOf(Color.parseColor("#001219"), Color.parseColor("#001F3F"), Color.parseColor("#000A14")),
                Color.parseColor("#0A2540"), Color.parseColor("#1B4965"), Color.parseColor("#CAF0F8"),
                Color.parseColor("#0077B6"), Color.parseColor("#48CAE4"), Color.parseColor("#48CAE4"),
                Color.parseColor("#001525"), Color.parseColor("#48CAE4"), Color.parseColor("#001525"),
                Color.parseColor("#000A14"), particleColor = Color.parseColor("#48CAE4")),
            // 6. Forest Emerald
            RealTheme("forest", "Forest Emerald", "🌲",
                intArrayOf(Color.parseColor("#001A0D"), Color.parseColor("#002E15"), Color.parseColor("#000F07")),
                Color.parseColor("#0A3D20"), Color.parseColor("#1B6B3A"), Color.parseColor("#E8F5E9"),
                Color.parseColor("#2E7D32"), Color.parseColor("#66BB6A"), Color.parseColor("#66BB6A"),
                Color.parseColor("#001A0D"), Color.parseColor("#81C784"), Color.parseColor("#001A0D"),
                Color.parseColor("#000F07"), particleColor = Color.parseColor("#66BB6A")),
            // 7. Royal Crimson
            RealTheme("crimson", "Royal Crimson", "👑",
                intArrayOf(Color.parseColor("#1A0005"), Color.parseColor("#2E000A"), Color.parseColor("#100003")),
                Color.parseColor("#3D0A15"), Color.parseColor("#6B1A2E"), Color.parseColor("#FFEBEE"),
                Color.parseColor("#C62828"), Color.parseColor("#FF1744"), Color.parseColor("#EF5350"),
                Color.parseColor("#1A0508"), Color.parseColor("#EF9A9A"), Color.parseColor("#1A0508"),
                Color.parseColor("#100003"), particleColor = Color.parseColor("#FF1744")),
            // 8. Arctic Ice
            RealTheme("arctic", "Arctic Ice", "❄️",
                intArrayOf(Color.parseColor("#E8F0FE"), Color.parseColor("#F0F4FF"), Color.parseColor("#DCE8FF")),
                Color.parseColor("#FFFFFF"), Color.parseColor("#BBDEFB"), Color.parseColor("#1A237E"),
                Color.parseColor("#1E88E5"), Color.parseColor("#64B5F6"), Color.parseColor("#90CAF9"),
                Color.parseColor("#E3F2FD"), Color.parseColor("#1565C0"), Color.parseColor("#E8EAF6"),
                Color.parseColor("#DCE8FF"), isLight = true, particleColor = Color.parseColor("#64B5F6")),
            // 9. Volcanic Fire
            RealTheme("volcanic", "Volcanic Fire", "🌋",
                intArrayOf(Color.parseColor("#1A0000"), Color.parseColor("#330000"), Color.parseColor("#0D0000")),
                Color.parseColor("#4A0E0E"), Color.parseColor("#8B0000"), Color.parseColor("#FFEBEE"),
                Color.parseColor("#FF6D00"), Color.parseColor("#FF6D00"), Color.parseColor("#FF6D00"),
                Color.parseColor("#1A0505"), Color.parseColor("#FF6D00"), Color.parseColor("#1A0505"),
                Color.parseColor("#0D0000"), particleColor = Color.parseColor("#FF6D00")),
            // 10. Sakura Pink
            RealTheme("sakura", "Sakura Pink", "🌸",
                intArrayOf(Color.parseColor("#1A0A15"), Color.parseColor("#2E1028"), Color.parseColor("#100810")),
                Color.parseColor("#3D1535"), Color.parseColor("#6B2860"), Color.parseColor("#FCE4EC"),
                Color.parseColor("#EC407A"), Color.parseColor("#FF4081"), Color.parseColor("#F48FB1"),
                Color.parseColor("#1A0A15"), Color.parseColor("#F48FB1"), Color.parseColor("#1A0A15"),
                Color.parseColor("#100810"), particleColor = Color.parseColor("#FF4081")),
            // 11. Midnight Sapphire
            RealTheme("sapphire", "Midnight Sapphire", "💎",
                intArrayOf(Color.parseColor("#000A1A"), Color.parseColor("#001035"), Color.parseColor("#000510")),
                Color.parseColor("#0A1E45"), Color.parseColor("#1A3A6B"), Color.parseColor("#E3F2FD"),
                Color.parseColor("#303F9F"), Color.parseColor("#536DFE"), Color.parseColor("#536DFE"),
                Color.parseColor("#000A1A"), Color.parseColor("#7986CB"), Color.parseColor("#000A1A"),
                Color.parseColor("#000510"), particleColor = Color.parseColor("#536DFE")),
            // 12. Diamond Platinum
            RealTheme("platinum", "Diamond Platinum", "💠",
                intArrayOf(Color.parseColor("#F5F5F5"), Color.parseColor("#FAFAFA"), Color.parseColor("#EEEEEE")),
                Color.parseColor("#FFFFFF"), Color.parseColor("#E0E0E0"), Color.parseColor("#212121"),
                Color.parseColor("#424242"), Color.parseColor("#BDBDBD"), Color.parseColor("#BDBDBD"),
                Color.parseColor("#ECEFF1"), Color.parseColor("#37474F"), Color.parseColor("#ECEFF1"),
                Color.parseColor("#E0E0E0"), isLight = true, particleColor = Color.parseColor("#BDBDBD")),
            // 13. Cherry Blossom
            RealTheme("cherry", "Cherry Blossom", "🌸",
                intArrayOf(Color.parseColor("#2D0A1E"), Color.parseColor("#4A1030"), Color.parseColor("#1A0815")),
                Color.parseColor("#5C1A42"), Color.parseColor("#8B2E6B"), Color.parseColor("#FFF0F5"),
                Color.parseColor("#FF69B4"), Color.parseColor("#FF1493"), Color.parseColor("#FF69B4"),
                Color.parseColor("#2D0A1E"), Color.parseColor("#FF69B4"), Color.parseColor("#2D0A1E"),
                Color.parseColor("#1A0815"), particleColor = Color.parseColor("#FFB6C1")),
            // 14. Golden Hour
            RealTheme("golden", "Golden Hour", "☀️",
                intArrayOf(Color.parseColor("#1A1000"), Color.parseColor("#2D1D00"), Color.parseColor("#120A00")),
                Color.parseColor("#3D2800"), Color.parseColor("#6B4500"), Color.parseColor("#FFFDE7"),
                Color.parseColor("#FFA000"), Color.parseColor("#FFD54F"), Color.parseColor("#FFB300"),
                Color.parseColor("#1A1000"), Color.parseColor("#FFD54F"), Color.parseColor("#1A1000"),
                Color.parseColor("#120A00"), particleColor = Color.parseColor("#FFD54F")),
            // 15. Deep Ocean
            RealTheme("deep_ocean", "Deep Ocean", "🐋",
                intArrayOf(Color.parseColor("#000D1A"), Color.parseColor("#001A33"), Color.parseColor("#000810")),
                Color.parseColor("#0A2040"), Color.parseColor("#153565"), Color.parseColor("#E0F2F1"),
                Color.parseColor("#00695C"), Color.parseColor("#26A69A"), Color.parseColor("#4DB6AC"),
                Color.parseColor("#000D1A"), Color.parseColor("#4DB6AC"), Color.parseColor("#000D1A"),
                Color.parseColor("#000810"), particleColor = Color.parseColor("#4DB6AC")),
            // 16. Rose Gold
            RealTheme("rose_gold", "Rose Gold", "🌹",
                intArrayOf(Color.parseColor("#1A0F12"), Color.parseColor("#2D1A20"), Color.parseColor("#100A0D")),
                Color.parseColor("#3D2030"), Color.parseColor("#6B3850"), Color.parseColor("#FFF0F0"),
                Color.parseColor("#B76E79"), Color.parseColor("#E8B4BC"), Color.parseColor("#D4A0A8"),
                Color.parseColor("#1A0F12"), Color.parseColor("#D4A0A8"), Color.parseColor("#1A0F12"),
                Color.parseColor("#100A0D"), particleColor = Color.parseColor("#E8B4BC")),
            // 17. Cyberpunk
            RealTheme("cyberpunk", "Cyberpunk", "🤖",
                intArrayOf(Color.parseColor("#0D001A"), Color.parseColor("#1A0033"), Color.parseColor("#080010")),
                Color.parseColor("#1F0040"), Color.parseColor("#3D0080"), Color.parseColor("#F5F5F5"),
                Color.parseColor("#FF00FF"), Color.parseColor("#00FFFF"), Color.parseColor("#FF00FF"),
                Color.parseColor("#0D001A"), Color.parseColor("#FF00FF"), Color.parseColor("#0D001A"),
                Color.parseColor("#080010"), particleColor = Color.parseColor("#00FFFF")),
            // 18. Midnight Rain
            RealTheme("rain", "Midnight Rain", "🌧️",
                intArrayOf(Color.parseColor("#0A0F1E"), Color.parseColor("#111B33"), Color.parseColor("#060A14")),
                Color.parseColor("#152040"), Color.parseColor("#253565"), Color.parseColor("#E3F2FD"),
                Color.parseColor("#42A5F5"), Color.parseColor("#90CAF9"), Color.parseColor("#64B5F6"),
                Color.parseColor("#0A0F1E"), Color.parseColor("#90CAF9"), Color.parseColor("#0A0F1E"),
                Color.parseColor("#060A14"), particleColor = Color.parseColor("#90CAF9")),
            // 19. Jade Mountain
            RealTheme("jade", "Jade Mountain", "🏔️",
                intArrayOf(Color.parseColor("#0A1A10"), Color.parseColor("#0F2E18"), Color.parseColor("#061008")),
                Color.parseColor("#103520"), Color.parseColor("#1B5E35"), Color.parseColor("#E8F5E9"),
                Color.parseColor("#388E3C"), Color.parseColor("#66BB6A"), Color.parseColor("#4CAF50"),
                Color.parseColor("#0A1A10"), Color.parseColor("#66BB6A"), Color.parseColor("#0A1A10"),
                Color.parseColor("#061008"), particleColor = Color.parseColor("#66BB6A")),
            // 20. Lavender Dream
            RealTheme("lavender", "Lavender Dream", "💜",
                intArrayOf(Color.parseColor("#1A0F2E"), Color.parseColor("#2D1A4A"), Color.parseColor("#100820")),
                Color.parseColor("#2A1548"), Color.parseColor("#4A2878"), Color.parseColor("#F3E5F5"),
                Color.parseColor("#7B1FA2"), Color.parseColor("#CE93D8"), Color.parseColor("#9C27B0"),
                Color.parseColor("#1A0F2E"), Color.parseColor("#CE93D8"), Color.parseColor("#1A0F2E"),
                Color.parseColor("#100820"), particleColor = Color.parseColor("#CE93D8")),
            // 21. Blood Moon
            RealTheme("blood_moon", "Blood Moon", "🌑",
                intArrayOf(Color.parseColor("#0D0000"), Color.parseColor("#1A0000"), Color.parseColor("#080000")),
                Color.parseColor("#2A0505"), Color.parseColor("#4A1010"), Color.parseColor("#FFEBEE"),
                Color.parseColor("#D32F2F"), Color.parseColor("#EF5350"), Color.parseColor("#C62828"),
                Color.parseColor("#0D0000"), Color.parseColor("#EF5350"), Color.parseColor("#0D0000"),
                Color.parseColor("#080000"), particleColor = Color.parseColor("#EF5350")),
            // 22. Ice Frost
            RealTheme("frost", "Ice Frost", "🧊",
                intArrayOf(Color.parseColor("#E0F7FA"), Color.parseColor("#E8F5E9"), Color.parseColor("#E3F2FD")),
                Color.parseColor("#FFFFFF"), Color.parseColor("#B2EBF2"), Color.parseColor("#006064"),
                Color.parseColor("#00BCD4"), Color.parseColor("#4DD0E1"), Color.parseColor("#00ACC1"),
                Color.parseColor("#E0F7FA"), Color.parseColor("#00838F"), Color.parseColor("#E0F7FA"),
                Color.parseColor("#E3F2FD"), isLight = true, particleColor = Color.parseColor("#4DD0E1")),
            // 23. Cosmic Nebula
            RealTheme("nebula", "Cosmic Nebula", "🌀",
                intArrayOf(Color.parseColor("#0D001A"), Color.parseColor("#1A0033"), Color.parseColor("#050010")),
                Color.parseColor("#1A0040"), Color.parseColor("#330080"), Color.parseColor("#EDE7F6"),
                Color.parseColor("#6200EA"), Color.parseColor("#B388FF"), Color.parseColor("#7C4DFF"),
                Color.parseColor("#0D001A"), Color.parseColor("#B388FF"), Color.parseColor("#0D001A"),
                Color.parseColor("#050010"), particleColor = Color.parseColor("#B388FF")),
            // 24. Autumn Leaves
            RealTheme("autumn", "Autumn Leaves", "🍂",
                intArrayOf(Color.parseColor("#1A0D00"), Color.parseColor("#2D1800"), Color.parseColor("#120800")),
                Color.parseColor("#3D2000"), Color.parseColor("#6B3800"), Color.parseColor("#FFF8E1"),
                Color.parseColor("#E65100"), Color.parseColor("#FF9800"), Color.parseColor("#F57C00"),
                Color.parseColor("#1A0D00"), Color.parseColor("#FF9800"), Color.parseColor("#1A0D00"),
                Color.parseColor("#120800"), particleColor = Color.parseColor("#FF9800")),

            // ═══ v4.0 — 20 NEW 8K 3D THEMES ═══
            // 25. Cyber Wave
            RealTheme("cyber_wave", "Cyber Wave", "🌊",
                intArrayOf(Color.parseColor("#0A001A"), Color.parseColor("#150033"), Color.parseColor("#050010")),
                Color.parseColor("#1A0040"), Color.parseColor("#2D0070"), Color.parseColor("#E0F7FA"),
                Color.parseColor("#00E5FF"), Color.parseColor("#18FFFF"), Color.parseColor("#00B8D4"),
                Color.parseColor("#0A001A"), Color.parseColor("#00E5FF"), Color.parseColor("#0A001A"),
                Color.parseColor("#050010"), is3DTheme = true, particleColor = Color.parseColor("#00E5FF")),
            // 26. Dragon Fire
            RealTheme("dragon_fire", "Dragon Fire", "🐉",
                intArrayOf(Color.parseColor("#1A0500"), Color.parseColor("#330A00"), Color.parseColor("#0D0200")),
                Color.parseColor("#4A1500"), Color.parseColor("#802500"), Color.parseColor("#FFF3E0"),
                Color.parseColor("#FF6D00"), Color.parseColor("#FF3D00"), Color.parseColor("#DD2C00"),
                Color.parseColor("#1A0500"), Color.parseColor("#FF6D00"), Color.parseColor("#1A0500"),
                Color.parseColor("#0D0200"), is3DTheme = true, particleColor = Color.parseColor("#FF3D00")),
            // 27. Moonlight
            RealTheme("moonlight", "Moonlight", "🌙",
                intArrayOf(Color.parseColor("#0A0F1E"), Color.parseColor("#111B33"), Color.parseColor("#060A14")),
                Color.parseColor("#1A2540"), Color.parseColor("#2A3560"), Color.parseColor("#E8EAF6"),
                Color.parseColor("#C5CAE9"), Color.parseColor("#9FA8DA"), Color.parseColor("#7986CB"),
                Color.parseColor("#0A0F1E"), Color.parseColor("#C5CAE9"), Color.parseColor("#0A0F1E"),
                Color.parseColor("#060A14"), is3DTheme = true, particleColor = Color.parseColor("#9FA8DA")),
            // 28. Thunder Storm
            RealTheme("thunder_storm", "Thunder Storm", "⛈️",
                intArrayOf(Color.parseColor("#0A0A14"), Color.parseColor("#141428"), Color.parseColor("#050510")),
                Color.parseColor("#1A1A35"), Color.parseColor("#2D2D55"), Color.parseColor("#F5F5FF"),
                Color.parseColor("#FFEA00"), Color.parseColor("#FFD600"), Color.parseColor("#FFFF00"),
                Color.parseColor("#0A0A14"), Color.parseColor("#FFEA00"), Color.parseColor("#0A0A14"),
                Color.parseColor("#050510"), is3DTheme = true, particleColor = Color.parseColor("#FFEA00")),
            // 29. Crystal Cave
            RealTheme("crystal_cave", "Crystal Cave", "💎",
                intArrayOf(Color.parseColor("#0A1A2E"), Color.parseColor("#0F2847"), Color.parseColor("#071220")),
                Color.parseColor("#153565"), Color.parseColor("#253565"), Color.parseColor("#E0F7FA"),
                Color.parseColor("#00BCD4"), Color.parseColor("#4DD0E1"), Color.parseColor("#80DEEA"),
                Color.parseColor("#0A1A2E"), Color.parseColor("#4DD0E1"), Color.parseColor("#0A1A2E"),
                Color.parseColor("#071220"), is3DTheme = true, particleColor = Color.parseColor("#80DEEA")),
            // 30. Northern Lights
            RealTheme("northern_lights", "Northern Lights", "🌌",
                intArrayOf(Color.parseColor("#050A14"), Color.parseColor("#0A1428"), Color.parseColor("#030510")),
                Color.parseColor("#0F1A35"), Color.parseColor("#1A3055"), Color.parseColor("#E8F5E9"),
                Color.parseColor("#00E676"), Color.parseColor("#69F0AE"), Color.parseColor("#B9F6CA"),
                Color.parseColor("#050A14"), Color.parseColor("#69F0AE"), Color.parseColor("#050A14"),
                Color.parseColor("#030510"), is3DTheme = true, particleColor = Color.parseColor("#69F0AE")),
            // 31. Desert Sand
            RealTheme("desert_sand", "Desert Sand", "🏜️",
                intArrayOf(Color.parseColor("#1A1000"), Color.parseColor("#332000"), Color.parseColor("#0D0800")),
                Color.parseColor("#4A3000"), Color.parseColor("#7A5000"), Color.parseColor("#FFF8E1"),
                Color.parseColor("#FFD54F"), Color.parseColor("#FFCA28"), Color.parseColor("#FFB300"),
                Color.parseColor("#1A1000"), Color.parseColor("#FFD54F"), Color.parseColor("#1A1000"),
                Color.parseColor("#0D0800"), is3DTheme = true, particleColor = Color.parseColor("#FFCA28")),
            // 32. Ocean Sunset
            RealTheme("ocean_sunset", "Ocean Sunset", "🌅",
                intArrayOf(Color.parseColor("#1A0A00"), Color.parseColor("#331500"), Color.parseColor("#0D0500")),
                Color.parseColor("#4A2000"), Color.parseColor("#803500"), Color.parseColor("#FFF3E0"),
                Color.parseColor("#FF7043"), Color.parseColor("#FF8A65"), Color.parseColor("#FFAB91"),
                Color.parseColor("#1A0A00"), Color.parseColor("#FF8A65"), Color.parseColor("#1A0A00"),
                Color.parseColor("#0D0500"), is3DTheme = true, particleColor = Color.parseColor("#FFAB91")),
            // 33. Midnight Rose
            RealTheme("midnight_rose", "Midnight Rose", "🌹",
                intArrayOf(Color.parseColor("#1A0010"), Color.parseColor("#330020"), Color.parseColor("#0D0008")),
                Color.parseColor("#4A0030"), Color.parseColor("#800050"), Color.parseColor("#FCE4EC"),
                Color.parseColor("#F06292"), Color.parseColor("#E91E63"), Color.parseColor("#C2185B"),
                Color.parseColor("#1A0010"), Color.parseColor("#F06292"), Color.parseColor("#1A0010"),
                Color.parseColor("#0D0008"), is3DTheme = true, particleColor = Color.parseColor("#E91E63")),
            // 34. Electric Blue
            RealTheme("electric_blue", "Electric Blue", "⚡",
                intArrayOf(Color.parseColor("#000A1A"), Color.parseColor("#001533"), Color.parseColor("#000510")),
                Color.parseColor("#0A1E45"), Color.parseColor("#1A3A70"), Color.parseColor("#E3F2FD"),
                Color.parseColor("#2979FF"), Color.parseColor("#448AFF"), Color.parseColor("#82B1FF"),
                Color.parseColor("#000A1A"), Color.parseColor("#448AFF"), Color.parseColor("#000A1A"),
                Color.parseColor("#000510"), is3DTheme = true, particleColor = Color.parseColor("#82B1FF")),
            // 35. Jade Emperor
            RealTheme("jade_emperor", "Jade Emperor", "👑",
                intArrayOf(Color.parseColor("#001A0A"), Color.parseColor("#003315"), Color.parseColor("#000F05")),
                Color.parseColor("#0A3D20"), Color.parseColor("#156B35"), Color.parseColor("#E8F5E9"),
                Color.parseColor("#00C853"), Color.parseColor("#69F0AE"), Color.parseColor("#B9F6CA"),
                Color.parseColor("#001A0A"), Color.parseColor("#69F0AE"), Color.parseColor("#001A0A"),
                Color.parseColor("#000F05"), is3DTheme = true, particleColor = Color.parseColor("#00C853")),
            // 36. Phoenix Flame
            RealTheme("phoenix_flame", "Phoenix Flame", "🔥",
                intArrayOf(Color.parseColor("#1A0500"), Color.parseColor("#331000"), Color.parseColor("#0D0200")),
                Color.parseColor("#4A1800"), Color.parseColor("#803000"), Color.parseColor("#FFF8E1"),
                Color.parseColor("#FF6F00"), Color.parseColor("#FF8F00"), Color.parseColor("#FFA000"),
                Color.parseColor("#1A0500"), Color.parseColor("#FF8F00"), Color.parseColor("#1A0500"),
                Color.parseColor("#0D0200"), is3DTheme = true, particleColor = Color.parseColor("#FF6F00")),
            // 37. Frozen Lake
            RealTheme("frozen_lake", "Frozen Lake", "🧊",
                intArrayOf(Color.parseColor("#E0F4FF"), Color.parseColor("#E8F8FF"), Color.parseColor("#D0EFFF")),
                Color.parseColor("#FFFFFF"), Color.parseColor("#B3E5FC"), Color.parseColor("#01579B"),
                Color.parseColor("#0288D1"), Color.parseColor("#03A9F4"), Color.parseColor("#4FC3F7"),
                Color.parseColor("#E0F4FF"), Color.parseColor("#0288D1"), Color.parseColor("#E0F4FF"),
                Color.parseColor("#D0EFFF"), isLight = true, is3DTheme = true, particleColor = Color.parseColor("#4FC3F7")),
            // 38. Rainbow Prism
            RealTheme("rainbow_prism", "Rainbow Prism", "🌈",
                intArrayOf(Color.parseColor("#0D001A"), Color.parseColor("#1A0033"), Color.parseColor("#080010")),
                Color.parseColor("#1F0040"), Color.parseColor("#3D0080"), Color.parseColor("#F5F5F5"),
                Color.parseColor("#E040FB"), Color.parseColor("#7C4DFF"), Color.parseColor("#536DFE"),
                Color.parseColor("#0D001A"), Color.parseColor("#E040FB"), Color.parseColor("#0D001A"),
                Color.parseColor("#080010"), is3DTheme = true, particleColor = Color.parseColor("#7C4DFF")),
            // 39. Black Hole
            RealTheme("black_hole", "Black Hole", "🕳️",
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#050505"), Color.parseColor("#000000")),
                Color.parseColor("#0A0A0A"), Color.parseColor("#151515"), Color.parseColor("#E0E0E0"),
                Color.parseColor("#9E9E9E"), Color.parseColor("#757575"), Color.parseColor("#616161"),
                Color.parseColor("#000000"), Color.parseColor("#9E9E9E"), Color.parseColor("#000000"),
                Color.parseColor("#000000"), is3DTheme = true, particleColor = Color.parseColor("#757575")),
            // 40. Supernova
            RealTheme("supernova", "Supernova", "💫",
                intArrayOf(Color.parseColor("#1A0A00"), Color.parseColor("#331500"), Color.parseColor("#0D0500")),
                Color.parseColor("#4A2000"), Color.parseColor("#803800"), Color.parseColor("#FFFDE7"),
                Color.parseColor("#FFC107"), Color.parseColor("#FFD54F"), Color.parseColor("#FFECB3"),
                Color.parseColor("#1A0A00"), Color.parseColor("#FFD54F"), Color.parseColor("#1A0A00"),
                Color.parseColor("#0D0500"), is3DTheme = true, particleColor = Color.parseColor("#FFC107")),
            // 41. Mystic Fog
            RealTheme("mystic_fog", "Mystic Fog", "🌫️",
                intArrayOf(Color.parseColor("#121620"), Color.parseColor("#1A2030"), Color.parseColor("#0A0E18")),
                Color.parseColor("#2A3045"), Color.parseColor("#3A4565"), Color.parseColor("#ECEFF1"),
                Color.parseColor("#90A4AE"), Color.parseColor("#B0BEC5"), Color.parseColor("#CFD8DC"),
                Color.parseColor("#121620"), Color.parseColor("#B0BEC5"), Color.parseColor("#121620"),
                Color.parseColor("#0A0E18"), is3DTheme = true, particleColor = Color.parseColor("#90A4AE")),
            // 42. Tropical Storm
            RealTheme("tropical_storm", "Tropical Storm", "🌀",
                intArrayOf(Color.parseColor("#001A1A"), Color.parseColor("#003333"), Color.parseColor("#000F0F")),
                Color.parseColor("#0A3535"), Color.parseColor("#156060"), Color.parseColor("#E0F7FA"),
                Color.parseColor("#00ACC1"), Color.parseColor("#26C6DA"), Color.parseColor("#4DD0E1"),
                Color.parseColor("#001A1A"), Color.parseColor("#26C6DA"), Color.parseColor("#001A1A"),
                Color.parseColor("#000F0F"), is3DTheme = true, particleColor = Color.parseColor("#00ACC1")),
            // 43. Silver Bullet
            RealTheme("silver_bullet", "Silver Bullet", "🔫",
                intArrayOf(Color.parseColor("#1A1A1A"), Color.parseColor("#2A2A2A"), Color.parseColor("#0F0F0F")),
                Color.parseColor("#3A3A3A"), Color.parseColor("#555555"), Color.parseColor("#FAFAFA"),
                Color.parseColor("#BDBDBD"), Color.parseColor("#E0E0E0"), Color.parseColor("#EEEEEE"),
                Color.parseColor("#1A1A1A"), Color.parseColor("#E0E0E0"), Color.parseColor("#1A1A1A"),
                Color.parseColor("#0F0F0F"), is3DTheme = true, particleColor = Color.parseColor("#BDBDBD")),
            // 44. Golden Dragon
            RealTheme("golden_dragon", "Golden Dragon", "🐲",
                intArrayOf(Color.parseColor("#1A1000"), Color.parseColor("#332000"), Color.parseColor("#0D0800")),
                Color.parseColor("#4A3000"), Color.parseColor("#7A5500"), Color.parseColor("#FFF8E1"),
                Color.parseColor("#FFB300"), Color.parseColor("#FFC107"), Color.parseColor("#FFD54F"),
                Color.parseColor("#1A1000"), Color.parseColor("#FFC107"), Color.parseColor("#1A1000"),
                Color.parseColor("#0D0800"), is3DTheme = true, particleColor = Color.parseColor("#FFB300"))
        )
    }

    fun createBackgroundDrawable(): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TL_BR, bgColors).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    }

    fun createKeyBackground(cornerRadiusPx: Float, pressed: Boolean = false): Drawable {
        if (is3DTheme || force3D) {
            // Draw a gorgeous skeuomorphic 3D keycap using LayerDrawable!
            val shadow = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = cornerRadiusPx
                setColor(borderColor) // Darker shadow color / border color
            }
            val front = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = cornerRadiusPx
                setColor(if (pressed) keyPressedColor else keyColor)
                setStroke(1, Color.argb(120, 255, 255, 255)) // shiny top bevel
            }
            val layers = arrayOf(shadow, front)
            val ld = LayerDrawable(layers)
            val shadowHeight = if (pressed) 1 else 6 // Pressed sits flatter, unpressed has a 6px 3D extrusion!
            ld.setLayerInset(1, 0, 0, 0, shadowHeight)
            return ld
        }
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusPx
            setColor(if (pressed) keyPressedColor else keyColor)
            setStroke(1, borderColor)
        }
    }

    fun createAccentBackground(cornerRadiusPx: Float): Drawable {
        if (is3DTheme || force3D) {
            val shadow = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = cornerRadiusPx
                setColor(borderColor)
            }
            val front = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = cornerRadiusPx
                setColor(accentColor)
                setStroke(1, Color.argb(120, 255, 255, 255))
            }
            val layers = arrayOf(shadow, front)
            val ld = LayerDrawable(layers)
            ld.setLayerInset(1, 0, 0, 0, 6)
            return ld
        }
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusPx
            setColor(accentColor)
        }
    }

    fun createGlowBackground(cornerRadiusPx: Float): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusPx
            setColor(glowColor)
            setStroke(2, Color.WHITE)
        }
    }
}

object RealThemeEngine {
    fun getAllThemes() = RealTheme.ALL
    fun getThemeById(id: String) = RealTheme.ALL.find { it.id == id }
    fun getThemeCount() = RealTheme.ALL.size
}
