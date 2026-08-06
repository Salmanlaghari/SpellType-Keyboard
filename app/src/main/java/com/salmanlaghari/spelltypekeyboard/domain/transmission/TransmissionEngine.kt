package com.salmanlaghari.spelltypekeyboard.domain.transmission

import android.util.Base64
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

/**
 * Transmission Engine — Smooth text style transfer & sharing system
 * Encode styled text for sharing, decode received styled text,
 * generate shareable URLs, QR code data, clipboard transmission
 */
object TransmissionEngine {

    // ═══════════════════════════════════════════
    //  DATA MODELS
    // ═══════════════════════════════════════════

    /** Represents a styled text payload ready for transmission */
    data class StyledPayload(
        val id: String = UUID.randomUUID().toString().take(8),
        val text: String,
        val unicodeStyle: String = "NONE",
        val frameStyle: String = "NONE",
        val shapeLayout: String = "NONE",
        val glitterEnabled: Boolean = false,
        val signature: String = "",
        val timestamp: Long = System.currentTimeMillis(),
        val version: Int = 1
    )

    /** Result of encoding a styled payload */
    data class EncodedPayload(
        val payload: StyledPayload,
        val encodedString: String,
        val shareableUrl: String,
        val qrData: String,
        val checksum: String
    )

    /** Result of decoding a received transmission */
    data class DecodedTransmission(
        val payload: StyledPayload?,
        val success: Boolean,
        val error: String? = null
    )

    /** Transmission method enum */
    enum class TransmissionMethod {
        CLIPBOARD,
        SHARE_URL,
        QR_CODE,
        DIRECT
    }

    /** Transmission history entry */
    data class TransmissionRecord(
        val id: String,
        val direction: Direction,
        val method: TransmissionMethod,
        val text: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    enum class Direction {
        SENT, RECEIVED
    }

    // ═══════════════════════════════════════════
    //  ENCODING
    // ═══════════════════════════════════════════

    /**
     * Encode styled text into a transmittable format
     */
    fun encode(payload: StyledPayload): EncodedPayload {
        val raw = buildString {
            append("STv${payload.version}|")
            append("${payload.text}|")
            append("${payload.unicodeStyle}|")
            append("${payload.frameStyle}|")
            append("${payload.shapeLayout}|")
            append("${if (payload.glitterEnabled) 1 else 0}|")
            append("${payload.signature}|")
            append(payload.timestamp)
        }

        val encodedString = Base64.encodeToString(
            raw.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP or Base64.URL_SAFE
        )

        val checksum = computeChecksum(raw)
        val encodedWithChecksum = "$encodedString.$checksum"

        val shareableUrl = buildShareableUrl(encodedWithChecksum)
        val qrData = buildQRData(encodedWithChecksum)

        return EncodedPayload(
            payload = payload,
            encodedString = encodedWithChecksum,
            shareableUrl = shareableUrl,
            qrData = qrData,
            checksum = checksum
        )
    }

    /**
     * Encode raw text with default styling for quick sharing
     */
    fun encodeQuick(text: String, unicodeStyle: String = "NONE"): EncodedPayload {
        return encode(StyledPayload(text = text, unicodeStyle = unicodeStyle))
    }

    // ═══════════════════════════════════════════
    //  DECODING
    // ═══════════════════════════════════════════

    /**
     * Decode a received transmission string back to styled payload
     */
    fun decode(encodedWithChecksum: String): DecodedTransmission {
        return try {
            val parts = encodedWithChecksum.split(".", limit = 2)
            if (parts.size != 2) {
                return DecodedTransmission(null, false, "Invalid format: missing checksum")
            }

            val encodedString = parts[0]
            val receivedChecksum = parts[1]

            val decodedBytes = Base64.decode(encodedString, Base64.NO_WRAP or Base64.URL_SAFE)
            val raw = String(decodedBytes, StandardCharsets.UTF_8)

            // Verify checksum
            val computedChecksum = computeChecksum(raw)
            if (computedChecksum != receivedChecksum) {
                return DecodedTransmission(null, false, "Checksum mismatch: data may be corrupted")
            }

            // Parse fields
            val fields = raw.split("|")
            if (fields.size < 8) {
                return DecodedTransmission(null, false, "Invalid payload: insufficient fields")
            }

            val versionStr = fields[0].removePrefix("STv")
            val version = versionStr.toIntOrNull() ?: 1

            val payload = StyledPayload(
                version = version,
                text = fields[1],
                unicodeStyle = fields[2],
                frameStyle = fields[3],
                shapeLayout = fields[4],
                glitterEnabled = fields[5] == "1",
                signature = fields[6],
                timestamp = fields[7].toLongOrNull() ?: System.currentTimeMillis()
            )

            DecodedTransmission(payload, true)
        } catch (e: Exception) {
            DecodedTransmission(null, false, "Decode error: ${e.message}")
        }
    }

    /**
     * Decode from a shareable URL
     */
    fun decodeFromUrl(url: String): DecodedTransmission {
        val data = extractDataFromUrl(url) ?: return DecodedTransmission(
            null, false, "Invalid URL: no transmission data found"
        )
        return decode(data)
    }

    // ═══════════════════════════════════════════
    //  SHAREABLE URL GENERATION
    // ═══════════════════════════════════════════

    /**
     * Generate a shareable URL with styled text embedded
     */
    private fun buildShareableUrl(encodedData: String): String {
        val safeData = URLEncoder.encode(encodedData, "UTF-8")
        return "https://spelltype.app/share?d=$safeData"
    }

    /**
     * Extract transmission data from a SpellType share URL
     */
    private fun extractDataFromUrl(url: String): String? {
        return try {
            val uri = java.net.URI(url)
            val query = uri.query ?: return null
            val params = query.split("&").associate {
                val (key, value) = it.split("=", limit = 2)
                key to java.net.URLDecoder.decode(value, "UTF-8")
            }
            params["d"]
        } catch (e: Exception) {
            null
        }
    }

    // ═══════════════════════════════════════════
    //  QR CODE DATA GENERATION
    // ═══════════════════════════════════════════

    /**
     * Generate QR code–compatible data string
     * Uses the shareable URL for maximum compatibility
     */
    fun buildQRData(encodedData: String): String {
        return buildShareableUrl(encodedData)
    }

    /**
     * Generate QR code data for a payload directly
     */
    fun generateQRData(payload: StyledPayload): String {
        val encoded = encode(payload)
        return encoded.qrData
    }

    /**
     * Generate a compact QR payload (shorter for small QR codes)
     * Uses just the encoded string without URL wrapper
     */
    fun generateCompactQRData(payload: StyledPayload): String {
        val encoded = encode(payload)
        return "ST:${encoded.encodedString}"
    }

    // ═══════════════════════════════════════════
    //  CLIPBOARD TRANSMISSION
    // ═══════════════════════════════════════════

    /**
     * Prepare clipboard data for transmission
     * Returns the encoded string ready to be placed on clipboard
     */
    fun prepareForClipboard(payload: StyledPayload): String {
        val encoded = encode(payload)
        return encoded.encodedString
    }

    /**
     * Prepare the styled text itself for direct clipboard paste
     * (just the rendered text, no encoding)
     */
    fun prepareDirectClipboard(payload: StyledPayload): String {
        return payload.text
    }

    /**
     * Parse clipboard content as a potential transmission
     */
    fun parseFromClipboard(clipboardText: String): DecodedTransmission {
        // Check if it's a URL
        if (clipboardText.startsWith("https://spelltype.app/share")) {
            return decodeFromUrl(clipboardText)
        }
        // Check if it's a compact QR format
        if (clipboardText.startsWith("ST:")) {
            return decode(clipboardText.removePrefix("ST:"))
        }
        // Try to decode as raw encoded data
        return decode(clipboardText)
    }

    // ═══════════════════════════════════════════
    //  CHECKSUM & VALIDATION
    // ═══════════════════════════════════════════

    /**
     * Compute SHA-256 checksum (first 8 hex chars)
     */
    private fun computeChecksum(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray(StandardCharsets.UTF_8))
        return hash.take(4).joinToString("") { "%02x".format(it) }
    }

    /**
     * Validate if a string looks like a valid transmission
     */
    fun isValidTransmission(data: String): Boolean {
        return try {
            if (data.startsWith("https://spelltype.app/share")) {
                val extracted = extractDataFromUrl(data) ?: return false
                val parts = extracted.split(".", limit = 2)
                parts.size == 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()
            } else if (data.startsWith("ST:")) {
                val parts = data.removePrefix("ST:").split(".", limit = 2)
                parts.size == 2
            } else {
                val parts = data.split(".", limit = 2)
                parts.size == 2 && Base64.decode(parts[0], Base64.NO_WRAP or Base64.URL_SAFE).isNotEmpty()
            }
        } catch (e: Exception) {
            false
        }
    }

    // ═══════════════════════════════════════════
    //  HISTORY TRACKING
    // ═══════════════════════════════════════════

    private val transmissionHistory = mutableListOf<TransmissionRecord>()

    /**
     * Record a transmission event
     */
    fun recordTransmission(
        direction: Direction,
        method: TransmissionMethod,
        text: String
    ): TransmissionRecord {
        val record = TransmissionRecord(
            id = UUID.randomUUID().toString().take(8),
            direction = direction,
            method = method,
            text = text
        )
        transmissionHistory.add(record)
        // Keep only last 100 records
        if (transmissionHistory.size > 100) {
            transmissionHistory.removeAt(0)
        }
        return record
    }

    /**
     * Get transmission history
     */
    fun getHistory(): List<TransmissionRecord> = transmissionHistory.toList()

    /**
     * Clear transmission history
     */
    fun clearHistory() = transmissionHistory.clear()
}
