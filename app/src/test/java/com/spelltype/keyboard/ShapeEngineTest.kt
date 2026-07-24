package com.spelltype.keyboard

import com.spelltype.keyboard.domain.ShapeEngine
import com.spelltype.keyboard.domain.model.ShapeLayout
import org.junit.Assert.*
import org.junit.Test

class ShapeEngineTest {

    @Test
    fun testEmptyInput() {
        assertEquals("", ShapeEngine.applyShape("", ShapeLayout.NONE))
        assertEquals("", ShapeEngine.applyShape("", ShapeLayout.PYRAMID))
        assertEquals("", ShapeEngine.applyShape("", ShapeLayout.HEART))
        assertEquals("", ShapeEngine.applyShape("", ShapeLayout.DIAMOND))
        assertEquals("", ShapeEngine.applyShape("", ShapeLayout.ZIGZAG))
        assertEquals("", ShapeEngine.applyShape("", ShapeLayout.WAVE))
    }

    @Test
    fun testNoneShape() {
        val input = "Hello"
        assertEquals(input, ShapeEngine.applyShape(input, ShapeLayout.NONE))
    }

    @Test
    fun testPyramidShape() {
        val input = "ABC"
        val result = ShapeEngine.applyShape(input, ShapeLayout.PYRAMID)
        // cleanText is "ABC"
        // Row 1: A
        // Row 2: B C
        // Result centered:
        //  A
        // B C
        val expected = " A\nB C"
        assertEquals(expected, result)
    }

    @Test
    fun testHeartShape() {
        val input = "♥"
        val result = ShapeEngine.applyShape(input, ShapeLayout.HEART)
        // Filled with "♥" - should match mask shape entirely
        assertTrue(result.contains("♥ ♥   ♥ ♥"))
    }

    @Test
    fun testDiamondShape() {
        val input = "A"
        val result = ShapeEngine.applyShape(input, ShapeLayout.DIAMOND)
        // CleanText "A" has length 1. Peak n = 1.
        // Line sizes: 1
        assertEquals("A", result)
    }

    @Test
    fun testZigzagShape() {
        val input = "ABC"
        val result = ShapeEngine.applyShape(input, ShapeLayout.ZIGZAG)
        // Rows = 3
        // Line 0: A
        // Line 1:  B
        // Line 2:   C
        val expected = "A  \n B \n  C"
        assertEquals(expected, result)
    }

    @Test
    fun testWaveShape() {
        val input = "ABC"
        val result = ShapeEngine.applyShape(input, ShapeLayout.WAVE)
        // Wave target rows: wavePattern[i % 4] where wavePattern = [1, 0, 1, 2]
        // 'A' (index 0) -> Row 1
        // 'B' (index 1) -> Row 0
        // 'C' (index 2) -> Row 1
        // Expected layout (3 rows):
        // Row 0:  B
        // Row 1: A C
        // Row 2:
        val expected = " B \nA C\n   "
        assertEquals(expected, result)
    }
}
