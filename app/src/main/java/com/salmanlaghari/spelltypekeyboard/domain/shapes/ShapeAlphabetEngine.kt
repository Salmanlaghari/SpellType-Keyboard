package com.salmanlaghari.spelltypekeyboard.domain.shapes

/**
 * 20+ 3D Shape Alphabet Engine
 * Transforms text into decorative Unicode shapes: Bubble, Heart, Diamond, Star, etc.
 */
object ShapeAlphabetEngine {

    enum class ShapeStyle(val displayName: String, val emoji: String) {
        BUBBLE("Bubble", "🫧"),
        HEART("Heart", "❤️"),
        DIAMOND("Diamond", "💎"),
        STAR("Star", "⭐"),
        CIRCLE("Circle", "⭕"),
        SQUARE("Square", "⬜"),
        TRIANGLE("Triangle", "🔺"),
        HEXAGON("Hexagon", "⬡"),
        FLAME("Flame", "🔥"),
        CROWN("Crown", "👑"),
        CLOUD("Cloud", "☁️"),
        MOON("Moon", "🌙"),
        SNOWFLAKE("Snowflake", "❄️"),
        LEAF("Leaf", "🍃"),
        CRYSTAL("Crystal", "🔮"),
        LIGHTNING("Lightning", "⚡"),
        MUSIC("Music", "🎵"),
        FLOWER("Flower", "🌸"),
        BUTTERFLY("Butterfly", "🦋"),
        OCEAN("Ocean", "🌊"),
        FIRE("Fire", "🔥"),
        GHOST("Ghost", "👻"),
        SKULL("Skull", "💀"),
        ALIEN("Alien", "👽"),
        ROBOT("Robot", "🤖"),
        NINJA("Ninja", "🥷"),
        PIRATE("Pirate", "🏴‍☠️"),
        UNICORN("Unicorn", "🦄"),
        DRAGON("Dragon", "🐉"),
        SPACE("Space", "🚀")
    }

    // Bubble Letters (Full alphabet)
    private val bubbleLower = mapOf(
        'a' to "ⓐ", 'b' to "ⓑ", 'c' to "ⓒ", 'd' to "ⓓ", 'e' to "ⓔ",
        'f' to "ⓕ", 'g' to "ⓖ", 'h' to "ⓗ", 'i' to "ⓘ", 'j' to "ⓙ",
        'k' to "ⓚ", 'l' to "ⓛ", 'm' to "ⓜ", 'n' to "ⓝ", 'o' to "ⓞ",
        'p' to "ⓟ", 'q' to "ⓠ", 'r' to "ⓡ", 's' to "ⓢ", 't' to "ⓣ",
        'u' to "ⓤ", 'v' to "ⓥ", 'w' to "ⓦ", 'x' to "ⓧ", 'y' to "ⓨ",
        'z' to "ⓩ"
    )
    private val bubbleUpper = mapOf(
        'A' to "Ⓐ", 'B' to "Ⓑ", 'C' to "Ⓒ", 'D' to "Ⓓ", 'E' to "Ⓔ",
        'F' to "Ⓕ", 'G' to "Ⓖ", 'H' to "Ⓗ", 'I' to "Ⓘ", 'J' to "Ⓙ",
        'K' to "Ⓚ", 'L' to "Ⓛ", 'M' to "Ⓜ", 'N' to "Ⓝ", 'O' to "Ⓞ",
        'P' to "Ⓟ", 'Q' to "Ⓠ", 'R' to "Ⓡ", 'S' to "Ⓢ", 'T' to "Ⓣ",
        'U' to "Ⓤ", 'V' to "Ⓥ", 'W' to "Ⓦ", 'X' to "Ⓧ", 'Y' to "Ⓨ",
        'Z' to "Ⓩ"
    )

    // Heart Letters
    private val heartLower = mapOf(
        'a' to "♥", 'b' to "♥", 'c' to "♥", 'd' to "♥", 'e' to "♥",
        'f' to "♥", 'g' to "♥", 'h' to "♥", 'i' to "♥", 'j' to "♥",
        'k' to "♥", 'l' to "♥", 'm' to "♥", 'n' to "♥", 'o' to "♥",
        'p' to "♥", 'q' to "♥", 'r' to "♥", 's' to "♥", 't' to "♥",
        'u' to "♥", 'v' to "♥", 'w' to "♥", 'x' to "♥", 'y' to "♥",
        'z' to "♥"
    )

    // Diamond Letters
    private val diamondLower = mapOf(
        'a' to "◆", 'b' to "◆", 'c' to "◆", 'd' to "◆", 'e' to "◆",
        'f' to "◆", 'g' to "◆", 'h' to "◆", 'i' to "◆", 'j' to "◆",
        'k' to "◆", 'l' to "◆", 'm' to "◆", 'n' to "◆", 'o' to "◆",
        'p' to "◆", 'q' to "◆", 'r' to "◆", 's' to "◆", 't' to "◆",
        'u' to "◆", 'v' to "◆", 'w' to "◆", 'x' to "◆", 'y' to "◆",
        'z' to "◆"
    )

    // Star Letters
    private val starLower = mapOf(
        'a' to "★", 'b' to "★", 'c' to "★", 'd' to "★", 'e' to "★",
        'f' to "★", 'g' to "★", 'h' to "★", 'i' to "★", 'j' to "★",
        'k' to "★", 'l' to "★", 'm' to "★", 'n' to "★", 'o' to "★",
        'p' to "★", 'q' to "★", 'r' to "★", 's' to "★", 't' to "★",
        'u' to "★", 'v' to "★", 'w' to "★", 'x' to "★", 'y' to "★",
        'z' to "★"
    )

    // Fancy Unicode Styles
    private val fancyStyles = mapOf(
        // Bold
        "bold" to mapOf(
            'a' to "𝐚", 'b' to "𝐛", 'c' to "𝐜", 'd' to "𝐝", 'e' to "𝐞",
            'f' to "𝐟", 'g' to "𝐠", 'h' to "𝐡", 'i' to "𝐢", 'j' to "𝐣",
            'k' to "𝐤", 'l' to "𝐥", 'm' to "𝐦", 'n' to "𝐧", 'o' to "𝐨",
            'p' to "𝐩", 'q' to "𝐪", 'r' to "𝐫", 's' to "𝐬", 't' to "𝐭",
            'u' to "𝐮", 'v' to "𝐯", 'w' to "𝐰", 'x' to "𝐱", 'y' to "𝐲",
            'z' to "𝐳"
        ),
        // Italic
        "italic" to mapOf(
            'a' to "𝘢", 'b' to "𝘣", 'c' to "𝘤", 'd' to "𝘥", 'e' to "𝘦",
            'f' to "𝘧", 'g' to "𝘨", 'h' to "𝘩", 'i' to "𝘪", 'j' to "𝘫",
            'k' to "𝘬", 'l' to "𝘭", 'm' to "𝘮", 'n' to "𝘯", 'o' to "𝘰",
            'p' to "𝘱", 'q' to "𝘲", 'r' to "𝘳", 's' to "𝘴", 't' to "𝘵",
            'u' to "𝘶", 'v' to "𝘷", 'w' to "𝘸", 'x' to "𝘹", 'y' to "𝘺",
            'z' to "𝘻"
        ),
        // Bold Italic
        "bold_italic" to mapOf(
            'a' to "𝙖", 'b' to "𝙗", 'c' to "𝙘", 'd' to "𝙙", 'e' to "𝙚",
            'f' to "𝙛", 'g' to "𝙜", 'h' to "𝙝", 'i' to "𝙞", 'j' to "𝙟",
            'k' to "𝙠", 'l' to "𝙡", 'm' to "𝙢", 'n' to "𝙣", 'o' to "𝙤",
            'p' to "𝙥", 'q' to "𝙦", 'r' to "𝙧", 's' to "𝙨", 't' to "𝙩",
            'u' to "𝙪", 'v' to "𝙫", 'w' to "𝙬", 'x' to "𝙭", 'y' to "𝙮",
            'z' to "𝙯"
        ),
        // Monospace
        "mono" to mapOf(
            'a' to "𝚊", 'b' to "𝚋", 'c' to "𝚌", 'd' to "𝚍", 'e' to "𝚎",
            'f' to "𝚏", 'g' to "𝚐", 'h' to "𝚑", 'i' to "𝚒", 'j' to "𝚓",
            'k' to "𝚔", 'l' to "𝚕", 'm' to "𝚖", 'n' to "𝚗", 'o' to "𝚘",
            'p' to "𝚙", 'q' to "𝚚", 'r' to "𝚛", 's' to "𝚜", 't' to "𝚝",
            'u' to "𝚞", 'v' to "𝚟", 'w' to "𝚠", 'x' to "𝚡", 'y' to "𝚢",
            'z' to "𝚣"
        ),
        // Double Struck
        "double" to mapOf(
            'a' to "𝕒", 'b' to "𝕓", 'c' to "𝕔", 'd' to "𝕕", 'e' to "𝕖",
            'f' to "𝕗", 'g' to "𝕘", 'h' to "𝕙", 'i' to "𝕚", 'j' to "𝕛",
            'k' to "𝕜", 'l' to "𝕝", 'm' to "𝕞", 'n' to "𝕟", 'o' to "𝕠",
            'p' to "𝕡", 'q' to "𝕢", 'r' to "𝕣", 's' to "𝕤", 't' to "𝕥",
            'u' to "𝕦", 'v' to "𝕧", 'w' to "𝕨", 'x' to "𝕩", 'y' to "𝕪",
            'z' to "𝕫"
        ),
        // Script
        "script" to mapOf(
            'a' to "𝒶", 'b' to "𝒷", 'c' to "𝒸", 'd' to "𝒹", 'e' to "𝑒",
            'f' to "𝒻", 'g' to "𝑔", 'h' to "𝒽", 'i' to "𝒾", 'j' to "𝒿",
            'k' to "𝓀", 'l' to "𝓁", 'm' to "𝓂", 'n' to "𝓃", 'o' to "𝑜",
            'p' to "𝓅", 'q' to "𝓆", 'r' to "𝓇", 's' to "𝓈", 't' to "𝓉",
            'u' to "𝓊", 'v' to "𝓋", 'w' to "𝓌", 'x' to "𝓍", 'y' to "𝓎",
            'z' to "𝓏"
        ),
        // Fraktur
        "fraktur" to mapOf(
            'a' to "𝔞", 'b' to "𝔟", 'c' to "𝔠", 'd' to "𝔡", 'e' to "𝔢",
            'f' to "𝔣", 'g' to "𝔤", 'h' to "𝔥", 'i' to "𝔦", 'j' to "𝔧",
            'k' to "𝔨", 'l' to "𝔩", 'm' to "𝔪", 'n' to "𝔫", 'o' to "𝔬",
            'p' to "𝔭", 'q' to "𝔮", 'r' to "𝔯", 's' to "𝔰", 't' to "𝔱",
            'u' to "𝔲", 'v' to "𝔳", 'w' to "𝔴", 'x' to "𝔵", 'y' to "𝔶",
            'z' to "𝔷"
        ),
        // Parenthesized
        "paren" to mapOf(
            'a' to "⒜", 'b' to "⒝", 'c' to "⒞", 'd' to "⒟", 'e' to "⒠",
            'f' to "⒡", 'g' to "⒢", 'h' to "⒣", 'i' to "⒤", 'j' to "⒥",
            'k' to "⒦", 'l' to "⒧", 'm' to "⒨", 'n' to "⒩", 'o' to "⒪",
            'p' to "⒫", 'q' to "⒬", 'r' to "⒭", 's' to "⒮", 't' to "⒯",
            'u' to "⒰", 'v' to "⒱", 'w' to "⒲", 'x' to "⒳", 'y' to "⒴",
            'z' to "⒵"
        )
    )

    /**
     * Apply shape transformation to text
     */
    fun applyShape(text: String, style: ShapeStyle): String {
        if (text.isEmpty()) return text

        return when (style) {
            ShapeStyle.BUBBLE -> text.map { ch ->
                when {
                    ch.isLowerCase() -> bubbleLower[ch] ?: ch.toString()
                    ch.isUpperCase() -> bubbleUpper[ch] ?: ch.toString()
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.HEART -> text.map { ch ->
                when {
                    ch.isLetter() -> "♥"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.DIAMOND -> text.map { ch ->
                when {
                    ch.isLetter() -> "◆"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.STAR -> text.map { ch ->
                when {
                    ch.isLetter() -> "★"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.CIRCLE -> text.map { ch ->
                when {
                    ch.isLetter() -> "●"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.SQUARE -> text.map { ch ->
                when {
                    ch.isLetter() -> "■"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.TRIANGLE -> text.map { ch ->
                when {
                    ch.isLetter() -> "▲"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.HEXAGON -> text.map { ch ->
                when {
                    ch.isLetter() -> "⬡"
                    else -> ch.toString()
                }
            }.joinToString("")
            ShapeStyle.FLAME -> "🔥 $text 🔥"
            ShapeStyle.CROWN -> "👑 $text 👑"
            ShapeStyle.CLOUD -> "☁️ $text ☁️"
            ShapeStyle.MOON -> "🌙 $text 🌙"
            ShapeStyle.SNOWFLAKE -> "❄️ $text ❄️"
            ShapeStyle.LEAF -> "🍃 $text 🍃"
            ShapeStyle.CRYSTAL -> "🔮 $text 🔮"
            ShapeStyle.LIGHTNING -> "⚡ $text ⚡"
            ShapeStyle.MUSIC -> "🎵 $text 🎵"
            ShapeStyle.FLOWER -> "🌸 $text 🌸"
            ShapeStyle.BUTTERFLY -> "🦋 $text 🦋"
            ShapeStyle.OCEAN -> "🌊 $text 🌊"
            ShapeStyle.FIRE -> "🔥 $text 🔥"
            ShapeStyle.GHOST -> "👻 $text 👻"
            ShapeStyle.SKULL -> "💀 $text 💀"
            ShapeStyle.ALIEN -> "👽 $text 👽"
            ShapeStyle.ROBOT -> "🤖 $text 🤖"
            ShapeStyle.NINJA -> "🥷 $text 🥷"
            ShapeStyle.PIRATE -> "🏴‍☠️ $text 🏴‍☠️"
            ShapeStyle.UNICORN -> "🦄 $text 🦄"
            ShapeStyle.DRAGON -> "🐉 $text 🐉"
            ShapeStyle.SPACE -> "🚀 $text 🚀"
        }
    }

    /**
     * Apply fancy Unicode style
     */
    fun applyFancyStyle(text: String, styleName: String): String {
        val style = fancyStyles[styleName] ?: return text
        return text.map { ch ->
            when {
                ch.isLowerCase() -> style[ch] ?: ch.toString()
                ch.isUpperCase() -> style[ch.lowercaseChar()]?.uppercase() ?: ch.toString()
                else -> ch.toString()
            }
        }.joinToString("")
    }

    fun getAllShapes(): List<ShapeStyle> = ShapeStyle.values().toList()
    fun getAllFancyStyles(): List<String> = fancyStyles.keys.toList()
}
