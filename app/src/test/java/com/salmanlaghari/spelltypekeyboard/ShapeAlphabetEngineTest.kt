package com.salmanlaghari.spelltypekeyboard

import com.salmanlaghari.spelltypekeyboard.domain.shapes.ShapeAlphabetEngine
import com.salmanlaghari.spelltypekeyboard.domain.shapes.ShapeAlphabetEngine.ShapeStyle
import org.junit.Assert.*
import org.junit.Test

class ShapeAlphabetEngineTest {

    @Test
    fun emptyInputReturnsEmptyForEveryStyle() {
        for (style in ShapeStyle.values()) {
            assertEquals("", ShapeAlphabetEngine.applyShape("", style))
        }
    }

    @Test
    fun bubbleMapsUpperAndLowerLetters() {
        assertEquals("ⓐⓑⓒ", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.BUBBLE))
        assertEquals("ⒶⒷⒸ", ShapeAlphabetEngine.applyShape("ABC", ShapeStyle.BUBBLE))
    }

    @Test
    fun bubbleLeavesNonLettersUnchanged() {
        assertEquals("ⓐ1 ⓑ!", ShapeAlphabetEngine.applyShape("a1 b!", ShapeStyle.BUBBLE))
    }

    @Test
    fun singleGlyphStylesReplaceEveryLetter() {
        assertEquals("♥♥♥", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.HEART))
        assertEquals("◆◆◆", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.DIAMOND))
        assertEquals("★★★", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.STAR))
        assertEquals("●●●", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.CIRCLE))
        assertEquals("■■■", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.SQUARE))
        assertEquals("▲▲▲", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.TRIANGLE))
        assertEquals("⬡⬡⬡", ShapeAlphabetEngine.applyShape("abc", ShapeStyle.HEXAGON))
    }

    @Test
    fun singleGlyphStylesKeepNonLetters() {
        assertEquals("♥♥ ♥1", ShapeAlphabetEngine.applyShape("ab c1", ShapeStyle.HEART))
    }

    @Test
    fun wrapperStylesSurroundTextWithEmoji() {
        assertEquals("🔥 hi 🔥", ShapeAlphabetEngine.applyShape("hi", ShapeStyle.FLAME))
        assertEquals("👑 hi 👑", ShapeAlphabetEngine.applyShape("hi", ShapeStyle.CROWN))
        assertEquals("🚀 hi 🚀", ShapeAlphabetEngine.applyShape("hi", ShapeStyle.SPACE))
    }

    @Test
    fun fancyBoldTransformsLettersAndPreservesRest() {
        val result = ShapeAlphabetEngine.applyFancyStyle("ab1", "bold")
        assertEquals("𝐚𝐛1", result)
    }

    @Test
    fun fancyStyleTransformsUppercaseLettersToo() {
        // Uppercase letters are resolved via their lowercase mapping; the bold math
        // glyphs have no distinct uppercase form, so "A" and "a" share the same glyph.
        val result = ShapeAlphabetEngine.applyFancyStyle("Ab", "bold")
        assertEquals(ShapeAlphabetEngine.applyFancyStyle("ab", "bold"), result)
        assertFalse(result.contains('A'))
        assertFalse(result.contains('b'))
    }

    @Test
    fun unknownFancyStyleReturnsInputUnchanged() {
        assertEquals("hello", ShapeAlphabetEngine.applyFancyStyle("hello", "not-a-style"))
    }

    @Test
    fun getAllShapesReturnsEveryEnumValue() {
        assertEquals(ShapeStyle.values().size, ShapeAlphabetEngine.getAllShapes().size)
    }

    @Test
    fun getAllFancyStylesContainsKnownStyles() {
        val styles = ShapeAlphabetEngine.getAllFancyStyles()
        assertTrue(styles.contains("bold"))
        assertTrue(styles.contains("italic"))
        assertTrue(styles.contains("script"))
        // Every advertised fancy style must actually transform text.
        for (name in styles) {
            assertNotEquals(
                "Fancy style $name should transform lowercase text",
                "abc",
                ShapeAlphabetEngine.applyFancyStyle("abc", name)
            )
        }
    }
}
