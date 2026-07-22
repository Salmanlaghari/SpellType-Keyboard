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
        assertNotEquals(input, result)
        assertTrue(result.contains("𝐀"))
        assertTrue(result.contains("𝐛"))
    }

    @Test
    fun testItalicStyle() {
        val input = "Ah"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.ITALIC)
        assertNotEquals(input, result)
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
    fun testSquaredSolidStyle() {
        val input = "A"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.SQUARED_SOLID)
        assertEquals("🅰", result)
    }

    @Test
    fun testBubbleStyle() {
        val input = "B"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.BUBBLE)
        assertEquals("𝔹", result)
    }

    @Test
    fun testFullWidthStyle() {
        val input = "Ab"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.FULL_WIDTH)
        assertEquals("Ａｂ", result)
    }

    @Test
    fun testStrikethroughStyle() {
        val input = "Ab"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.STRIKETHROUGH)
        assertTrue(result.contains("\u0336"))
    }

    @Test
    fun testUnderlineStyle() {
        val input = "Ab"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.UNDERLINE)
        assertTrue(result.contains("\u0332"))
    }

    @Test
    fun testSuperscriptStyle() {
        val input = "0"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.SUPERSCRIPT)
        assertEquals("⁰", result)
    }

    @Test
    fun testSubscriptStyle() {
        val input = "0"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.SUBSCRIPT)
        assertEquals("₀", result)
    }

    @Test
    fun testBoldItalicStyle() {
        val input = "A"
        val result = UnicodeStylingEngine.applyStyle(input, UnicodeStyle.BOLD_ITALIC)
        assertEquals("𝑨", result)
    }
}
