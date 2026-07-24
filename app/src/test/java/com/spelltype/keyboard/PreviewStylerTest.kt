package com.spelltype.keyboard

import com.spelltype.keyboard.domain.PreviewStyler
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PreviewStylerTest {

    @Test
    fun testEmptyInput() {
        val result = PreviewStyler.stylePreview("", colorfulEnabled = true, giantWordsEnabled = true)
        assertEquals("", result.toString())
    }

    @Test
    fun testStandardNoStyling() {
        val text = "Hello World"
        val result = PreviewStyler.stylePreview(text, colorfulEnabled = false, giantWordsEnabled = false)
        assertEquals(text, result.toString())
    }

    @Test
    fun testColorfulRainbowSpans() {
        val text = "Ok"
        val result = PreviewStyler.stylePreview(text, colorfulEnabled = true, giantWordsEnabled = false)
        assertTrue(result is Spanned)
        val spanned = result as Spanned

        // Assert that ForegroundColorSpan exists on the spanned preview
        val spans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        assertTrue(spans.isNotEmpty())
    }

    @Test
    fun testGiantWordsSpans() {
        val text = "Large Word"
        val result = PreviewStyler.stylePreview(text, colorfulEnabled = false, giantWordsEnabled = true)
        assertTrue(result is Spanned)
        val spanned = result as Spanned

        // Assert that AbsoluteSizeSpan exists on the spanned preview
        val spans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        assertTrue(spans.isNotEmpty())
    }
}
