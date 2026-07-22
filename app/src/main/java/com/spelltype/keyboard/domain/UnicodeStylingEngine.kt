package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.UnicodeStyle

object UnicodeStylingEngine {
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
        }
    }

    private fun codePointToString(cp: Int): String {
        return String(Character.toChars(cp))
    }
}
