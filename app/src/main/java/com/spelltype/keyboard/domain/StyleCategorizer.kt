package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.FrameStyle

object StyleCategorizer {

    private val classicStyles = setOf(
        FrameStyle.NONE, FrameStyle.BOX, FrameStyle.BOX_DOUBLE, FrameStyle.BOX_ROUNDED,
        FrameStyle.DOTTED, FrameStyle.DASHED, FrameStyle.CORNER, FrameStyle.BRACKET
    )

    private val symbolStyles = setOf(
        FrameStyle.STAR, FrameStyle.DIAMOND, FrameStyle.HEARTS, FrameStyle.FLOWERS,
        FrameStyle.MUSIC, FrameStyle.SNOWFLAKE, FrameStyle.CROSS, FrameStyle.ARROW
    )

    private val premiumStyles = setOf(
        FrameStyle.GEM, FrameStyle.CROWN, FrameStyle.GALAXY, FrameStyle.FIRE, FrameStyle.BOX_DOUBLE
    )

    fun getCategory(style: FrameStyle): String {
        return when {
            style in classicStyles -> "Classic"
            style in symbolStyles -> "Symbol"
            else -> "Emoji"
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
            "Emoji" -> allStyles.filter { it !in classicStyles && it !in symbolStyles }
            else -> allStyles.toList()
        }
    }

    fun getAllCategories(): List<String> {
        return listOf("Classic", "Symbol", "Emoji")
    }
}
