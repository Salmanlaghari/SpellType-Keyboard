package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.UnicodeStyle

object UnicodeStylingEngine {

    private val superscriptMap = mapOf(
        '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴",
        '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹",
        'a' to "ᵃ", 'b' to "ᵇ", 'c' to "ᶜ", 'd' to "ᵈ", 'e' to "ᵉ",
        'f' to "ᶠ", 'g' to "ᵍ", 'h' to "ʰ", 'i' to "ⁱ", 'j' to "ʲ",
        'k' to "ᵏ", 'l' to "ˡ", 'm' to "ᵐ", 'n' to "ⁿ", 'o' to "ᵒ",
        'p' to "ᵖ", 'r' to "ʳ", 's' to "ˢ", 't' to "ᵗ", 'u' to "ᵘ",
        'v' to "ᵛ", 'w' to "ʷ", 'x' to "ˣ", 'y' to "ʸ", 'z' to "ᶻ",
        'A' to "ᴬ", 'B' to "ᴮ", 'D' to "ᴰ", 'E' to "ᴱ", 'G' to "ᴳ",
        'H' to "ᴴ", 'I' to "ᴵ", 'J' to "ᴶ", 'K' to "ᴲ", 'L' to "ᴸ",
        'M' to "ᴹ", 'N' to "ᴺ", 'O' to "ᴼ", 'P' to "ᴾ", 'R' to "ᴿ",
        'T' to "ᵀ", 'U' to "ᵁ", 'V' to "ⱽ", 'W' to "ᵂ"
    )

    private val subscriptMap = mapOf(
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄",
        '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉",
        'a' to "ₐ", 'e' to "ₑ", 'h' to "ₕ", 'i' to "ᵢ", 'j' to "ⱼ",
        'k' to "ₖ", 'l' to "ₗ", 'm' to "ₘ", 'n' to "ₙ", 'o' to "ₒ",
        'p' to "ₚ", 'r' to "ᵣ", 's' to "ₛ", 't' to "ₜ", 'u' to "ᵤ",
        'v' to "ᵥ", 'x' to "ₓ"
    )

    fun applyStyle(text: String, style: UnicodeStyle): String {
        if (style == UnicodeStyle.NONE) return text

        return text.map { char ->
            getStyledChar(char, style)
        }.joinToString("")
    }

    private fun getStyledChar(char: Char, style: UnicodeStyle): String {
        return when (style) {
            UnicodeStyle.NONE -> char.toString()
            UnicodeStyle.BOLD -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1D400 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1D41A + (char - 'a'))
                    in '0'..'9' -> codePointToString(0x1D7CE + (char - '0'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.ITALIC -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1D434 + (char - 'A'))
                    in 'a'..'z' -> {
                        if (char == 'h') codePointToString(0x210E)
                        else codePointToString(0x1D44E + (char - 'a'))
                    }
                    else -> char.toString()
                }
            }
            UnicodeStyle.GOTHIC -> {
                when (char) {
                    in 'A'..'Z' -> {
                        when (char) {
                            'C' -> codePointToString(0x212D)
                            'H' -> codePointToString(0x210C)
                            'I' -> codePointToString(0x2111)
                            'R' -> codePointToString(0x211C)
                            'Z' -> codePointToString(0x2128)
                            else -> codePointToString(0x1D504 + (char - 'A'))
                        }
                    }
                    in 'a'..'z' -> codePointToString(0x1D51E + (char - 'a'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.CURSIVE -> {
                when (char) {
                    in 'A'..'Z' -> {
                        when (char) {
                            'B' -> codePointToString(0x212C)
                            'E' -> codePointToString(0x2130)
                            'F' -> codePointToString(0x2131)
                            'H' -> codePointToString(0x210B)
                            'I' -> codePointToString(0x2110)
                            'L' -> codePointToString(0x2112)
                            'M' -> codePointToString(0x2133)
                            'R' -> codePointToString(0x211B)
                            else -> codePointToString(0x1D49C + (char - 'A'))
                        }
                    }
                    in 'a'..'z' -> {
                        when (char) {
                            'e' -> codePointToString(0x212F)
                            'g' -> codePointToString(0x210A)
                            'o' -> codePointToString(0x2134)
                            else -> codePointToString(0x1D4B6 + (char - 'a'))
                        }
                    }
                    else -> char.toString()
                }
            }
            UnicodeStyle.CIRCLED -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x24B6 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x24D0 + (char - 'a'))
                    '0' -> codePointToString(0x24EA)
                    in '1'..'9' -> codePointToString(0x2460 + (char - '1'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.SQUARED -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1F130 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1F130 + (char - 'a'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.SQUARED_SOLID -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1F170 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1F170 + (char - 'a'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.BUBBLE -> {
                when (char) {
                    in 'A'..'Z' -> {
                        when (char) {
                            'C' -> codePointToString(0x2102)
                            'H' -> codePointToString(0x210D)
                            'N' -> codePointToString(0x2115)
                            'P' -> codePointToString(0x2119)
                            'Q' -> codePointToString(0x211A)
                            'R' -> codePointToString(0x211D)
                            'Z' -> codePointToString(0x2124)
                            else -> codePointToString(0x1D538 + (char - 'A'))
                        }
                    }
                    in 'a'..'z' -> codePointToString(0x1D552 + (char - 'a'))
                    in '0'..'9' -> codePointToString(0x1D7D8 + (char - '0'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.FULL_WIDTH -> {
                when (char) {
                    in '!'..'~' -> codePointToString(0xFF01 + (char - '!'))
                    ' ' -> "　"
                    else -> char.toString()
                }
            }
            UnicodeStyle.STRIKETHROUGH -> {
                if (char.isWhitespace()) char.toString() else "$char\u0336"
            }
            UnicodeStyle.UNDERLINE -> {
                if (char.isWhitespace()) char.toString() else "$char\u0332"
            }
            UnicodeStyle.SUPERSCRIPT -> {
                superscriptMap[char] ?: char.toString()
            }
            UnicodeStyle.SUBSCRIPT -> {
                subscriptMap[char] ?: char.toString()
            }
            UnicodeStyle.BOLD_ITALIC -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1D468 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1D482 + (char - 'a'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.MONOSPACE -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1D670 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1D68A + (char - 'a'))
                    in '0'..'9' -> codePointToString(0x1D7F6 + (char - '0'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.CIRCLED_NEGATIVE -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1F150 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1F150 + (char - 'a'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.REGIONAL_INDICATOR -> {
                when (char) {
                    in 'A'..'Z' -> codePointToString(0x1F1E6 + (char - 'A'))
                    in 'a'..'z' -> codePointToString(0x1F1E6 + (char - 'a'))
                    else -> char.toString()
                }
            }
            UnicodeStyle.GIANT_WORDS -> {
                val upper = char.uppercaseChar()
                when (upper) {
                    in 'A'..'Z' -> codePointToString(0xFF21 + (upper - 'A'))
                    in '0'..'9' -> codePointToString(0xFF10 + (upper - '0'))
                    else -> upper.toString()
                }
            }
        }
    }

    private fun codePointToString(cp: Int): String {
        return String(Character.toChars(cp))
    }
}
