package com.salmanlaghari.spelltypekeyboard

import com.salmanlaghari.spelltypekeyboard.domain.StyleCategorizer
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import org.junit.Assert.*
import org.junit.Test

class StyleCategorizerTest {

    @Test
    fun everyStyleResolvesToACategory() {
        for (style in FrameStyle.values()) {
            val category = StyleCategorizer.getCategory(style)
            assertTrue(
                "Category for $style should not be blank",
                category.isNotBlank()
            )
        }
    }

    @Test
    fun knownStylesMapToExpectedCategories() {
        assertEquals("Classic", StyleCategorizer.getCategory(FrameStyle.NONE))
        assertEquals("Classic", StyleCategorizer.getCategory(FrameStyle.BOX))
        assertEquals("Symbol", StyleCategorizer.getCategory(FrameStyle.STAR))
        assertEquals("Emoji", StyleCategorizer.getCategory(FrameStyle.CROWN))
        assertEquals("♥ Love", StyleCategorizer.getCategory(FrameStyle.LOVE_MODE))
        assertEquals("🌿 Nature", StyleCategorizer.getCategory(FrameStyle.CHERRY_BLOSSOM))
        assertEquals("🌧️ Weather", StyleCategorizer.getCategory(FrameStyle.RAIN_MODE))
        assertEquals("💀 Dark", StyleCategorizer.getCategory(FrameStyle.GOTHIC_MODE))
        assertEquals("🎉 Festival", StyleCategorizer.getCategory(FrameStyle.CHRISTMAS_MODE))
        assertEquals("🔥 Power", StyleCategorizer.getCategory(FrameStyle.DRAGON_MODE))
        assertEquals("🎮 Modern", StyleCategorizer.getCategory(FrameStyle.GAMER_MODE))
        assertEquals("🎈 Fun", StyleCategorizer.getCategory(FrameStyle.MUSIC_MODE))
    }

    @Test
    fun getCategoryIsConsistentWithGetStylesByCategory() {
        for (category in StyleCategorizer.getAllCategories()) {
            val styles = StyleCategorizer.getStylesByCategory(category)
            assertTrue(
                "Category $category should contain at least one style",
                styles.isNotEmpty()
            )
            for (style in styles) {
                assertEquals(
                    "Style $style listed under $category should categorize back to it",
                    category,
                    StyleCategorizer.getCategory(style)
                )
            }
        }
    }

    @Test
    fun unknownCategoryReturnsAllStyles() {
        val result = StyleCategorizer.getStylesByCategory("does-not-exist")
        assertEquals(FrameStyle.values().size, result.size)
    }

    @Test
    fun premiumFlagIsConsistent() {
        assertTrue(StyleCategorizer.isPremium(FrameStyle.GEM))
        assertTrue(StyleCategorizer.isPremium(FrameStyle.DRAGON_MODE))
        assertFalse(StyleCategorizer.isPremium(FrameStyle.NONE))
    }

    @Test
    fun getAllCategoriesHasNoDuplicates() {
        val categories = StyleCategorizer.getAllCategories()
        assertEquals(categories.size, categories.toSet().size)
    }
}
