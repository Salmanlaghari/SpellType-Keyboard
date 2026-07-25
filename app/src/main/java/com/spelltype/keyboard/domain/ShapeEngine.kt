package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.ShapeLayout

object ShapeEngine {
    fun applyShape(text: String, shape: ShapeLayout): String {
        if (text.isEmpty()) return ""
        val cleanText = text.replace(" ", "")
        if (cleanText.isEmpty()) return ""

        return when (shape) {
            ShapeLayout.NONE -> text
            ShapeLayout.PYRAMID -> {
                val lines = mutableListOf<String>()
                var charIndex = 0
                var lineNum = 1
                while (charIndex < cleanText.length) {
                    val lineChars = mutableListOf<Char>()
                    for (j in 0 until lineNum) {
                        lineChars.add(cleanText[charIndex % cleanText.length])
                        charIndex++
                    }
                    lines.add(lineChars.joinToString(" "))
                    lineNum++
                }
                val maxLen = lines.last().length
                lines.map { line ->
                    val spaces = " ".repeat((maxLen - line.length) / 2)
                    spaces + line
                }.joinToString("\n")
            }
            ShapeLayout.DIAMOND -> {
                val len = cleanText.length
                var n = 1
                while (n * n < len) {
                    n++
                }
                val lineSizes = mutableListOf<Int>()
                for (i in 1..n) {
                    lineSizes.add(i)
                }
                for (i in n - 1 downTo 1) {
                    lineSizes.add(i)
                }
                var index = 0
                val lines = mutableListOf<String>()
                for (size in lineSizes) {
                    val lineChars = mutableListOf<Char>()
                    for (j in 0 until size) {
                        lineChars.add(cleanText[index % cleanText.length])
                        index++
                    }
                    lines.add(lineChars.joinToString(" "))
                }
                val maxLen = lines.maxOf { it.length }
                lines.map { line ->
                    val spaces = " ".repeat((maxLen - line.length) / 2)
                    spaces + line
                }.joinToString("\n")
            }
            ShapeLayout.HEART -> {
                val mask = listOf(
                    "  x x   x x  ",
                    "x x x x x x x",
                    "x x x x x x x",
                    "  x x x x x  ",
                    "    x x x    ",
                    "      x      "
                )
                var index = 0
                val resultLines = mask.map { row ->
                    val sb = StringBuilder()
                    for (char in row) {
                        if (char == 'x') {
                            sb.append(cleanText[index % cleanText.length])
                            index++
                        } else {
                            sb.append(char)
                        }
                    }
                    sb.toString()
                }
                resultLines.joinToString("\n")
            }
            ShapeLayout.ZIGZAG -> {
                val rows = 3
                val resultLists = List(rows) { StringBuilder() }
                var row = 0
                var direction = 1
                for (char in cleanText) {
                    for (r in 0 until rows) {
                        if (r == row) {
                            resultLists[r].append(char)
                        } else {
                            resultLists[r].append(" ")
                        }
                    }
                    if (rows > 1) {
                        row += direction
                        if (row == rows - 1) {
                            direction = -1
                        } else if (row == 0) {
                            direction = 1
                        }
                    }
                }
                resultLists.joinToString("\n") { it.toString() }
            }
            ShapeLayout.WAVE -> {
                val rows = 3
                val resultLists = List(rows) { StringBuilder() }
                val wavePattern = listOf(1, 0, 1, 2)
                for (i in cleanText.indices) {
                    val char = cleanText[i]
                    val targetRow = wavePattern[i % wavePattern.size]
                    for (r in 0 until rows) {
                        if (r == targetRow) {
                            resultLists[r].append(char)
                        } else {
                            resultLists[r].append(" ")
                        }
                    }
                }
                resultLists.joinToString("\n") { it.toString() }
            }
            ShapeLayout.CIRCLE -> {
                val mask = listOf(
                    "  x x x  ",
                    " x x x x ",
                    "x x x x x",
                    " x x x x ",
                    "  x x x  "
                )
                var index = 0
                val resultLines = mask.map { row ->
                    val sb = StringBuilder()
                    for (char in row) {
                        if (char == 'x') {
                            sb.append(cleanText[index % cleanText.length])
                            index++
                        } else {
                            sb.append(char)
                        }
                    }
                    sb.toString()
                }
                resultLines.joinToString("\n")
            }
            ShapeLayout.LOVE -> {
                val mask = listOf(
                    " (♡_♡) (♡_♡) ",
                    "  x x x x x  ",
                    "   x x x x   ",
                    "    x x x    ",
                    "     x x     ",
                    "      x      "
                )
                var index = 0
                val resultLines = mask.map { row ->
                    val sb = StringBuilder()
                    for (char in row) {
                        if (char == 'x') {
                            sb.append(cleanText[index % cleanText.length])
                            index++
                        } else {
                            sb.append(char)
                        }
                    }
                    sb.toString()
                }
                resultLines.joinToString("\n")
            }
            ShapeLayout.REVENGE -> {
                val bannerTop = "☠️ REVENGE ☠️\n"
                val bannerBottom = "\n⚔️⚔️⚔️⚔️⚔️⚔️"
                val lines = cleanText.chunked(12)
                val body = lines.take(3).joinToString("\n") { "  $it  " }
                bannerTop + body + bannerBottom
            }
            ShapeLayout.PUBG -> {
                val bannerTop = "🍳 AIRDROP 🍳\n"
                val bannerBottom = "\n🏆 WINNER 🏆"
                val lines = cleanText.chunked(12)
                val body = lines.take(3).joinToString("\n") { "  $it  " }
                bannerTop + body + bannerBottom
            }
            ShapeLayout.SOCIAL_MEDIA -> {
                val bannerTop = "📢 COMMENT 📢\n"
                val bannerBottom = "\n👍 LIKE & SHARE 🔔"
                val lines = cleanText.chunked(14)
                val body = lines.take(3).joinToString("\n") { "  $it  " }
                bannerTop + body + bannerBottom
            }
        }
    }
}
