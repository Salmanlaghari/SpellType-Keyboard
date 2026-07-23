package com.spelltype.keyboard.domain

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan

object PreviewStyler {

    private val rainbowColors = listOf(
        Color.parseColor("#EF4444"), // Red
        Color.parseColor("#F97316"), // Orange
        Color.parseColor("#F59E0B"), // Yellow
        Color.parseColor("#10B981"), // Green
        Color.parseColor("#3B82F6"), // Blue
        Color.parseColor("#8B5CF6")  // Purple
    )

    private val fontSizesSp = listOf(14, 18, 24, 32)

    fun stylePreview(
        text: String,
        colorfulEnabled: Boolean,
        giantWordsEnabled: Boolean
    ): CharSequence {
        if (text.isEmpty()) return ""
        if (!colorfulEnabled && !giantWordsEnabled) return text

        val builder = SpannableStringBuilder()
        val lines = text.split("\n")

        for (l in lines.indices) {
            val line = lines[l]
            val words = line.split(" ")

            for (w in words.indices) {
                val word = words[w]
                if (word.isEmpty()) {
                    if (w < words.size - 1) builder.append(" ")
                    continue
                }

                val start = builder.length
                builder.append(word)
                val end = builder.length

                // Apply Giant Words sizing on word level
                if (giantWordsEnabled) {
                    val sizeSp = fontSizesSp[w % fontSizesSp.size]
                    builder.setSpan(
                        AbsoluteSizeSpan(sizeSp, true),
                        start,
                        end,
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                // Apply Colorful rainbow colors on character level (smooth gradient)
                if (colorfulEnabled) {
                    for (i in start until end) {
                        val colorIndex = (i - start) % rainbowColors.size
                        builder.setSpan(
                            ForegroundColorSpan(rainbowColors[colorIndex]),
                            i,
                            i + 1,
                            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                if (w < words.size - 1) {
                    builder.append(" ")
                }
            }
            if (l < lines.size - 1) {
                builder.append("\n")
            }
        }

        return builder
    }
}
