package com.spelltype.keyboard

import com.spelltype.keyboard.domain.UnicodeStylingEngine
import com.spelltype.keyboard.domain.model.UnicodeStyle
import org.junit.Assert.*
import org.junit.Test

class UnicodeStylingEngineTest {

    @Test
    fun testNoneStyle() {
        val input = "Hello 123"
        assertEquals(input, UnicodeStylingEngine.applyStyle(input, UnicodeStyle.NONE))
    }

    @Test
    fun testBoldStyle() {
        val input = "Ab"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.BOLD)
        // Check that result is styled
        assertNotEquals(input, result)
        assertTrue(result.contains("𝐀")) // bold capital A
        assertTrue(result.contains("𝐛")) // bold small b
    }

    @Test
    fun testItalicStyle() {
        val input = "Ah"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.ITALIC)
        assertNotEquals(input, result)
        // italic small h has code point U+210E
        assertTrue(result.contains("ℎ") || result.contains("𝘈"))
    }

    @Test
    fun testCircledStyle() {
        val input = "A"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.CIRCLED)
        assertEquals("Ⓐ", result)
    }

    @Test
    fun testSquaredStyle() {
        val input = "A"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.SQUARED)
        assertEquals("🄰", result)
    }

    @Test
    fun testBubbleStyle() {
        val input = "B"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.BUBBLE)
        assertEquals("𝔹", result)
    }

    @Test
    fun testSquaredSolidStyle() {
        val input = "A"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.SQUARED_SOLID)
        assertEquals("🅰", result)
    }
}
