package com.salmanlaghari.spelltypekeyboard

import com.salmanlaghari.spelltypekeyboard.domain.ArtEngine
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import org.junit.Assert.*
import org.junit.Test

class ArtEngineTest {

    @Test
    fun testEmptyInput() {
        assertEquals("", ArtEngine.applyFrame("", FrameStyle.NONE))
        assertEquals("", ArtEngine.applyFrame("", FrameStyle.BOX))
        assertEquals("", ArtEngine.applyFrame("", FrameStyle.STAR))
        assertEquals("", ArtEngine.applyFrame("", FrameStyle.BRACKET))
        assertEquals("", ArtEngine.applyFrame("", FrameStyle.DIAMOND))
    }

    @Test
    fun testNoneStyle() {
        val input = "Hello World"
        val result = ArtEngine.applyFrame(input, FrameStyle.NONE)
        assertEquals(input, result)
    }

    @Test
    fun testBoxStyleSingleLine() {
        val input = "Hi"
        val result = ArtEngine.applyFrame(input, FrameStyle.BOX)
        assertEquals("┌─ Hi ─┐", result)
    }

    @Test
    fun testBoxDoubleStyle() {
        val input = "Hello"
        val result = ArtEngine.applyFrame(input, FrameStyle.BOX_DOUBLE)
        assertEquals("╔═ Hello ═╗", result)
    }

    @Test
    fun testStarStyle() {
        val input = "Ok"
        val result = ArtEngine.applyFrame(input, FrameStyle.STAR)
        assertEquals("★★★ Ok ★★★", result)
    }

    @Test
    fun testBracketStyle() {
        val input = "Awesome"
        val result = ArtEngine.applyFrame(input, FrameStyle.BRACKET)
        assertEquals("【 Awesome 】", result)
    }

    @Test
    fun testDiamondStyle() {
        val input = "Yay"
        val result = ArtEngine.applyFrame(input, FrameStyle.DIAMOND)
        assertEquals("◆◆◆ Yay ◆◆◆", result)
    }

    @Test
    fun testFloralStyle() {
        val input = "Hello"
        val result = ArtEngine.applyFrame(input, FrameStyle.FLORAL)
        assertTrue(result.contains("✿"))
        assertTrue(result.contains("Hello"))
    }

    @Test
    fun testGemStyle() {
        val input = "Hi"
        val result = ArtEngine.applyFrame(input, FrameStyle.GEM)
        assertTrue(result.contains("💎"))
        assertTrue(result.contains("Hi"))
    }

    @Test
    fun testCloverStyle() {
        val input = "Test"
        val result = ArtEngine.applyFrame(input, FrameStyle.CLOVER)
        assertTrue(result.contains("🍀"))
        assertTrue(result.contains("Test"))
    }

    @Test
    fun testArrowStyle() {
        val input = "Go"
        val result = ArtEngine.applyFrame(input, FrameStyle.ARROW)
        assertEquals("◀══ Go ══▶", result)
    }

    @Test
    fun testHeartsStyle() {
        val input = "Love"
        val result = ArtEngine.applyFrame(input, FrameStyle.HEARTS)
        assertEquals("♥♥♥ Love ♥♥♥", result)
    }
}
