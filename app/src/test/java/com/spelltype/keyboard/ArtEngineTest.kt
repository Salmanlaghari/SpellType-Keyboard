package com.spelltype.keyboard

import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.model.FrameStyle
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
        val expected = "┌────┐\n│ Hi │\n└────┘"
        assertEquals(expected, result)
    }

    @Test
    fun testBoxStyleMultiLine() {
        val input = "Go\nTeam"
        val result = ArtEngine.applyFrame(input, FrameStyle.BOX)
        val expected = "┌──────┐\n│ Go   │\n│ Team │\n└──────┘"
        assertEquals(expected, result)
    }

    @Test
    fun testStarStyle() {
        val input = "Ok"
        val result = ArtEngine.applyFrame(input, FrameStyle.STAR)
        val expected = "★★★★★★\n★ Ok ★\n★★★★★★"
        assertEquals(expected, result)
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
        val expected = "◆◆◆◆◆◆◆\n◆ Yay ◆\n◆◆◆◆◆◆◆"
        assertEquals(expected, result)
    }
}
