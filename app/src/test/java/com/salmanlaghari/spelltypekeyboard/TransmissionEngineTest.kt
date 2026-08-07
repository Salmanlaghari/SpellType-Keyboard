package com.salmanlaghari.spelltypekeyboard

import com.salmanlaghari.spelltypekeyboard.domain.transmission.TransmissionEngine
import com.salmanlaghari.spelltypekeyboard.domain.transmission.TransmissionEngine.Direction
import com.salmanlaghari.spelltypekeyboard.domain.transmission.TransmissionEngine.StyledPayload
import com.salmanlaghari.spelltypekeyboard.domain.transmission.TransmissionEngine.TransmissionMethod
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TransmissionEngineTest {

    private fun samplePayload() = StyledPayload(
        text = "Hello World",
        unicodeStyle = "BOLD",
        frameStyle = "STAR",
        shapeLayout = "HEART",
        glitterEnabled = true,
        signature = "sig",
        timestamp = 1_700_000_000_000L
    )

    @Before
    @After
    fun resetHistory() {
        TransmissionEngine.clearHistory()
    }

    @Test
    fun encodeThenDecodeRoundTripsAllFields() {
        val payload = samplePayload()
        val encoded = TransmissionEngine.encode(payload)

        val decoded = TransmissionEngine.decode(encoded.encodedString)
        assertTrue(decoded.success)
        assertNull(decoded.error)
        val result = decoded.payload!!
        assertEquals(payload.text, result.text)
        assertEquals(payload.unicodeStyle, result.unicodeStyle)
        assertEquals(payload.frameStyle, result.frameStyle)
        assertEquals(payload.shapeLayout, result.shapeLayout)
        assertEquals(payload.glitterEnabled, result.glitterEnabled)
        assertEquals(payload.signature, result.signature)
        assertEquals(payload.timestamp, result.timestamp)
        assertEquals(payload.version, result.version)
    }

    @Test
    fun encodedStringCarriesChecksumSuffix() {
        val encoded = TransmissionEngine.encode(samplePayload())
        assertTrue(encoded.encodedString.endsWith(".${encoded.checksum}"))
    }

    @Test
    fun decodeRejectsMissingChecksum() {
        val decoded = TransmissionEngine.decode("no-checksum-here")
        assertFalse(decoded.success)
        assertNull(decoded.payload)
        assertNotNull(decoded.error)
    }

    @Test
    fun decodeDetectsChecksumMismatch() {
        val encoded = TransmissionEngine.encode(samplePayload())
        val base = encoded.encodedString.substringBeforeLast(".")
        val tampered = "$base.deadbeef"

        val decoded = TransmissionEngine.decode(tampered)
        assertFalse(decoded.success)
        assertNull(decoded.payload)
        assertTrue(decoded.error!!.contains("Checksum"))
    }

    @Test
    fun shareableUrlRoundTrips() {
        val encoded = TransmissionEngine.encode(samplePayload())
        val decoded = TransmissionEngine.decodeFromUrl(encoded.shareableUrl)
        assertTrue(decoded.success)
        assertEquals("Hello World", decoded.payload!!.text)
    }

    @Test
    fun decodeFromInvalidUrlFails() {
        val decoded = TransmissionEngine.decodeFromUrl("https://example.com/no-data")
        assertFalse(decoded.success)
    }

    @Test
    fun compactQrDataRoundTripsViaClipboardParser() {
        val payload = samplePayload()
        val compact = TransmissionEngine.generateCompactQRData(payload)
        assertTrue(compact.startsWith("ST:"))

        val decoded = TransmissionEngine.parseFromClipboard(compact)
        assertTrue(decoded.success)
        assertEquals(payload.text, decoded.payload!!.text)
    }

    @Test
    fun parseFromClipboardHandlesUrlAndRawForms() {
        val encoded = TransmissionEngine.encode(samplePayload())

        val fromUrl = TransmissionEngine.parseFromClipboard(encoded.shareableUrl)
        assertTrue(fromUrl.success)

        val fromRaw = TransmissionEngine.parseFromClipboard(encoded.encodedString)
        assertTrue(fromRaw.success)
    }

    @Test
    fun prepareForClipboardMatchesEncodedString() {
        val payload = samplePayload()
        assertEquals(
            TransmissionEngine.encode(payload).encodedString,
            TransmissionEngine.prepareForClipboard(payload)
        )
    }

    @Test
    fun prepareDirectClipboardReturnsRawText() {
        assertEquals("Hello World", TransmissionEngine.prepareDirectClipboard(samplePayload()))
    }

    @Test
    fun isValidTransmissionAcceptsAllEncodedForms() {
        val encoded = TransmissionEngine.encode(samplePayload())
        assertTrue(TransmissionEngine.isValidTransmission(encoded.encodedString))
        assertTrue(TransmissionEngine.isValidTransmission(encoded.shareableUrl))
        assertTrue(TransmissionEngine.isValidTransmission("ST:${encoded.encodedString}"))
    }

    @Test
    fun isValidTransmissionRejectsGarbage() {
        assertFalse(TransmissionEngine.isValidTransmission("just some text"))
        assertFalse(TransmissionEngine.isValidTransmission("https://spelltype.app/share?x=1"))
    }

    @Test
    fun encodeQuickUsesProvidedStyleAndDefaults() {
        val encoded = TransmissionEngine.encodeQuick("hi", unicodeStyle = "ITALIC")
        val decoded = TransmissionEngine.decode(encoded.encodedString)
        assertTrue(decoded.success)
        assertEquals("hi", decoded.payload!!.text)
        assertEquals("ITALIC", decoded.payload!!.unicodeStyle)
        assertEquals("NONE", decoded.payload!!.frameStyle)
    }

    @Test
    fun recordTransmissionAppendsToHistory() {
        assertTrue(TransmissionEngine.getHistory().isEmpty())
        val record = TransmissionEngine.recordTransmission(
            Direction.SENT, TransmissionMethod.CLIPBOARD, "hello"
        )
        val history = TransmissionEngine.getHistory()
        assertEquals(1, history.size)
        assertEquals(record.id, history[0].id)
        assertEquals(Direction.SENT, history[0].direction)
        assertEquals(TransmissionMethod.CLIPBOARD, history[0].method)
    }

    @Test
    fun historyIsCappedAtOneHundredEntries() {
        repeat(105) {
            TransmissionEngine.recordTransmission(
                Direction.RECEIVED, TransmissionMethod.DIRECT, "msg-$it"
            )
        }
        assertEquals(100, TransmissionEngine.getHistory().size)
        // Oldest entries are dropped, so the most recent message survives.
        assertEquals("msg-104", TransmissionEngine.getHistory().last().text)
    }

    @Test
    fun clearHistoryEmptiesHistory() {
        TransmissionEngine.recordTransmission(Direction.SENT, TransmissionMethod.QR_CODE, "x")
        TransmissionEngine.clearHistory()
        assertTrue(TransmissionEngine.getHistory().isEmpty())
    }
}
