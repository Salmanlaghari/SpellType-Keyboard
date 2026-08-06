package com.salmanlaghari.spelltypekeyboard.domain.effects

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.View

/**
 * Rain & Cherry Blossom Particle Effects Engine
 * Creates beautiful 8K HD rain drops and cherry blossom petals on keyboard
 */
object ParticleEffectsEngine {

    private var isActive = false
    private var particleView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private val particles = mutableListOf<Particle>()
    private var effectType = EffectType.NONE

    enum class EffectType {
        NONE, RAIN, CHERRY_BLOSSOM, SNOW, SPARKLE, FIRE, STARS
    }

    data class Particle(
        var x: Float,
        var y: Float,
        var speed: Float,
        var size: Float,
        var alpha: Int,
        var color: Int,
        var rotation: Float = 0f,
        var type: EffectType
    )

    private val rainColors = intArrayOf(
        Color.parseColor("#4FC3F7"), // Light blue
        Color.parseColor("#29B6F6"), // Blue
        Color.parseColor("#81D4FA"), // Pale blue
        Color.parseColor("#B3E5FC"), // Very light blue
        Color.parseColor("#E1F5FE")  // Almost white blue
    )

    private val cherryBlossomColors = intArrayOf(
        Color.parseColor("#FFB7C5"), // Sakura pink
        Color.parseColor("#FF9EB5"), // Dark pink
        Color.parseColor("#FFC1CC"), // Light pink
        Color.parseColor("#FFDEE6"), // Very light pink
        Color.parseColor("#FFFFFF"), // White petal
        Color.parseColor("#F8BBD0")  // Medium pink
    )

    private val snowColors = intArrayOf(
        Color.WHITE,
        Color.parseColor("#E3F2FD"),
        Color.parseColor("#BBDEFB"),
        Color.parseColor("#E8EAF6")
    )

    private val sparkleColors = intArrayOf(
        Color.parseColor("#FFD700"), // Gold
        Color.parseColor("#FFF176"), // Yellow
        Color.parseColor("#FFAB00"), // Amber
        Color.parseColor("#FFFFFF"), // White
        Color.parseColor("#E040FB")  // Purple
    )

    private val fireColors = intArrayOf(
        Color.parseColor("#FF6D00"), // Orange
        Color.parseColor("#FF9100"), // Light orange
        Color.parseColor("#FF3D00"), // Red-orange
        Color.parseColor("#FFD600"), // Yellow
        Color.parseColor("#FF1744")  // Red
    )

    private val starColors = intArrayOf(
        Color.parseColor("#FFD700"), // Gold
        Color.parseColor("#FFF176"), // Yellow
        Color.parseColor("#FFFFFF"), // White
        Color.parseColor("#CE93D8")  // Purple
    )

    fun startRain(view: View) {
        effectType = EffectType.RAIN
        startEffect(view)
    }

    fun startCherryBlossom(view: View) {
        effectType = EffectType.CHERRY_BLOSSOM
        startEffect(view)
    }

    fun startSnow(view: View) {
        effectType = EffectType.SNOW
        startEffect(view)
    }

    fun startSparkle(view: View) {
        effectType = EffectType.SPARKLE
        startEffect(view)
    }

    fun startFire(view: View) {
        effectType = EffectType.FIRE
        startEffect(view)
    }

    fun startStars(view: View) {
        effectType = EffectType.STARS
        startEffect(view)
    }

    fun stop() {
        isActive = false
        particles.clear()
        particleView?.invalidate()
        handler.removeCallbacksAndMessages(null)
    }

    fun isActive(): Boolean = isActive

    private fun startEffect(view: View) {
        stop()
        isActive = true
        particleView = view
        spawnParticles()
        animateLoop()
    }

    private fun spawnParticles() {
        val view = particleView ?: return
        val width = view.width.toFloat().coerceAtLeast(1f)
        val height = view.height.toFloat().coerceAtLeast(1f)

        val count = when (effectType) {
            EffectType.RAIN -> 40
            EffectType.CHERRY_BLOSSOM -> 25
            EffectType.SNOW -> 30
            EffectType.SPARKLE -> 20
            EffectType.FIRE -> 15
            EffectType.STARS -> 15
            EffectType.NONE -> 0
        }

        val colors = when (effectType) {
            EffectType.RAIN -> rainColors
            EffectType.CHERRY_BLOSSOM -> cherryBlossomColors
            EffectType.SNOW -> snowColors
            EffectType.SPARKLE -> sparkleColors
            EffectType.FIRE -> fireColors
            EffectType.STARS -> starColors
            EffectType.NONE -> return
        }

        for (i in 0 until count) {
            particles.add(Particle(
                x = (Math.random() * width).toFloat(),
                y = (Math.random() * height * -1).toFloat(), // Start above
                speed = (2f + Math.random() * 4f).toFloat(),
                size = (3f + Math.random() * 8f).toFloat(),
                alpha = (150 + Math.random() * 105).toInt().coerceIn(0, 255),
                color = colors.random(),
                rotation = (Math.random() * 360f).toFloat(),
                type = effectType
            ))
        }
    }

    private fun animateLoop() {
        if (!isActive) return
        val view = particleView ?: return

        handler.postDelayed({
            updateParticles(view)
            view.invalidate()
            animateLoop()
        }, 16) // ~60fps update rate
    }

    private fun updateParticles(view: View) {
        val width = view.width.toFloat()
        val height = view.height.toFloat()

        for (p in particles) {
            when (p.type) {
                EffectType.RAIN -> {
                    p.y += p.speed * 2f // Fast fall
                    p.x += 0.5f // Slight wind
                    if (p.y > height) {
                        p.y = -10f
                        p.x = (Math.random() * width).toFloat()
                    }
                }
                EffectType.CHERRY_BLOSSOM -> {
                    p.y += p.speed * 0.7f // Slow gentle fall
                    p.x += Math.sin(p.y * 0.02).toFloat() * 1.5f // Sway
                    p.rotation += 1.5f // Rotating petal
                    if (p.y > height) {
                        p.y = -20f
                        p.x = (Math.random() * width).toFloat()
                    }
                }
                EffectType.SNOW -> {
                    p.y += p.speed * 0.5f
                    p.x += Math.sin(p.y * 0.03).toFloat() * 0.8f
                    if (p.y > height) {
                        p.y = -10f
                        p.x = (Math.random() * width).toFloat()
                    }
                }
                EffectType.SPARKLE -> {
                    p.y += p.speed * 0.3f
                    p.alpha = (p.alpha + ((Math.random() * 20 - 10).toInt())).coerceIn(50, 255)
                    p.size = (p.size + (Math.random() * 0.4 - 0.2).toFloat()).coerceIn(2f, 12f)
                    if (p.y > height) {
                        p.y = -5f
                        p.x = (Math.random() * width).toFloat()
                    }
                }
                EffectType.FIRE -> {
                    p.y -= p.speed * 1.5f // Fire goes UP
                    p.x += Math.sin(p.y * 0.05).toFloat() * 2f
                    p.alpha = (p.alpha - 3).coerceIn(0, 255)
                    p.size = (p.size - 0.1f).coerceAtLeast(0f)
                    if (p.alpha <= 0 || p.y < -10f) {
                        p.y = height + 10f
                        p.x = (Math.random() * width).toFloat()
                        p.alpha = 255
                        p.size = (3f + Math.random() * 8f).toFloat()
                    }
                }
                EffectType.STARS -> {
                    p.alpha = (p.alpha + ((Math.random() * 30 - 15).toInt())).coerceIn(30, 255)
                    p.size = (p.size + Math.sin(System.currentTimeMillis() * 0.001 + p.x).toFloat() * 0.5f).coerceIn(2f, 10f)
                }
                EffectType.NONE -> {}
            }
        }

        // Remove dead particles and respawn
        particles.removeAll { it.alpha <= 0 && it.type != EffectType.STARS }
        if (particles.size < 15) spawnParticles()
    }

    fun drawParticles(canvas: Canvas) {
        if (!isActive || effectType == EffectType.NONE) return

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        for (p in particles) {
            paint.color = p.color
            paint.alpha = p.alpha.coerceIn(0, 255)

            when (p.type) {
                EffectType.RAIN -> {
                    // Draw rain drop — thin elongated line
                    paint.strokeWidth = p.size * 0.3f
                    paint.strokeCap = Paint.Cap.ROUND
                    canvas.drawLine(p.x, p.y, p.x + 1f, p.y + p.size * 3f, paint)
                }
                EffectType.CHERRY_BLOSSOM -> {
                    // Draw petal — small circle with soft edges
                    paint.shader = RadialGradient(
                        p.x, p.y, p.size,
                        intArrayOf(p.color, Color.TRANSPARENT),
                        floatArrayOf(0.3f, 1f),
                        Shader.TileMode.CLAMP
                    )
                    canvas.save()
                    canvas.rotate(p.rotation, p.x, p.y)
                    canvas.drawOval(
                        RectF(p.x - p.size, p.y - p.size * 0.6f, p.x + p.size, p.y + p.size * 0.6f),
                        paint
                    )
                    canvas.restore()
                    paint.shader = null
                }
                EffectType.SNOW -> {
                    // Draw snowflake — small circle
                    canvas.drawCircle(p.x, p.y, p.size * 0.5f, paint)
                }
                EffectType.SPARKLE -> {
                    // Draw sparkle — star shape (cross)
                    paint.strokeWidth = 1.5f
                    val s = p.size
                    canvas.drawLine(p.x - s, p.y, p.x + s, p.y, paint)
                    canvas.drawLine(p.x, p.y - s, p.x, p.y + s, paint)
                    canvas.drawLine(p.x - s * 0.7f, p.y - s * 0.7f, p.x + s * 0.7f, p.y + s * 0.7f, paint)
                    canvas.drawLine(p.x + s * 0.7f, p.y - s * 0.7f, p.x - s * 0.7f, p.y + s * 0.7f, paint)
                }
                EffectType.FIRE -> {
                    // Draw fire — gradient circle
                    paint.shader = RadialGradient(
                        p.x, p.y, p.size,
                        intArrayOf(Color.YELLOW, Color.parseColor("#FF6D00"), Color.TRANSPARENT),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(p.x, p.y, p.size, paint)
                    paint.shader = null
                }
                EffectType.STARS -> {
                    // Draw star — small twinkling dot
                    paint.shader = RadialGradient(
                        p.x, p.y, p.size,
                        intArrayOf(Color.WHITE, p.color, Color.TRANSPARENT),
                        floatArrayOf(0f, 0.4f, 1f),
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(p.x, p.y, p.size, paint)
                    paint.shader = null
                }
                EffectType.NONE -> {}
            }
        }
    }

    fun getEffectName(): String {
        return when (effectType) {
            EffectType.RAIN -> "🌧️ Rain"
            EffectType.CHERRY_BLOSSOM -> "🌸 Cherry Blossom"
            EffectType.SNOW -> "❄️ Snow"
            EffectType.SPARKLE -> "✨ Sparkle"
            EffectType.FIRE -> "🔥 Fire"
            EffectType.STARS -> "⭐ Stars"
            EffectType.NONE -> "None"
        }
    }
}
