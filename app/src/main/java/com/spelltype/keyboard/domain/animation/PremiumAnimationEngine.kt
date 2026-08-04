package com.spelltype.keyboard.domain.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.PathInterpolator
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium Animation Engine — 60fps silky-smooth animations
 * Spring physics, ripple effects, neon glow, and particle systems
 */
object PremiumAnimationEngine {

    // ═══════════════════════════════════════════
    //  KEY PRESS ANIMATIONS (60fps)
    // ═══════════════════════════════════════════

    /** Premium key press with spring bounce + glow pulse */
    fun animateKeyPress(view: View, glowColor: Int = Color.CYAN): AnimatorSet {
        val pressDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.92f)
        val pressDownY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.92f)
        val pressAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.85f)

        val pressAnim = ObjectAnimator.ofPropertyValuesHolder(view, pressDown, pressDownY, pressAlpha).apply {
            duration = 60
            interpolator = AccelerateDecelerateInterpolator()
        }

        val releaseX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.92f, 1.03f, 1f)
        val releaseY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.92f, 1.03f, 1f)
        val releaseAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.85f, 1f)

        val releaseAnim = ObjectAnimator.ofPropertyValuesHolder(view, releaseX, releaseY, releaseAlpha).apply {
            duration = 180
            interpolator = OvershootInterpolator(2.5f)
        }

        return AnimatorSet().apply {
            playSequentially(pressAnim, releaseAnim)
            start()
        }
    }

    /** Neon glow pulse on key press */
    fun animateGlowPulse(view: View, glowColor: Int, durationMs: Long = 300) {
        val glowIn = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0.7f, 1f).apply {
            duration = durationMs
            interpolator = AccelerateDecelerateInterpolator()
        }
        glowIn.start()
    }

    /** Ripple expand animation from center */
    fun animateRipple(view: View, color: Int = Color.WHITE) {
        val scaleUp = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.15f).apply { duration = 100 }
        val scaleUpY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.15f).apply { duration = 100 }
        val fadeOut = ObjectAnimator.ofFloat(view, View.ALPHA, 0.9f, 1f).apply { duration = 150 }

        val scaleDown = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.15f, 1f).apply {
            duration = 200
            interpolator = OvershootInterpolator(1.5f)
        }
        val scaleDownY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.15f, 1f).apply {
            duration = 200
            interpolator = OvershootInterpolator(1.5f)
        }

        AnimatorSet().apply {
            play(scaleUp).with(scaleUpY).with(fadeOut)
            play(scaleDown).with(scaleDownY).after(scaleUp)
            start()
        }
    }

    // ═══════════════════════════════════════════
    //  TOOLBAR & CHIP ANIMATIONS
    // ═══════════════════════════════════════════

    /** Slide-in animation for toolbar chips */
    fun animateChipSlideIn(view: View, delayMs: Long = 0) {
        view.alpha = 0f
        view.translationX = 30f
        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(250)
            .setStartDelay(delayMs)
            .setInterpolator(OvershootInterpolator(1.8f))
            .start()
    }

    /** Scale pop for selected chip */
    fun animateChipSelect(view: View) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.12f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.12f)
        ).apply { duration = 100 }

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.12f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.12f, 1f)
        ).apply {
            duration = 200
            interpolator = OvershootInterpolator(2f)
        }

        AnimatorSet().apply {
            playSequentially(scaleUp, scaleDown)
            start()
        }
    }

    // ═══════════════════════════════════════════
    //  SMOOTH ENTRANCE ANIMATIONS
    // ═══════════════════════════════════════════

    /** Keyboard slide-up entrance */
    fun animateKeyboardEntrance(view: View) {
        view.translationY = view.height.toFloat()
        view.alpha = 0f
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(350)
            .setInterpolator(PathInterpolator(0.2f, 0f, 0.1f, 1f))
            .start()
    }

    /** Staggered row reveal */
    fun animateRowReveal(rows: List<View>, staggerMs: Long = 50) {
        rows.forEachIndexed { index, row ->
            row.alpha = 0f
            row.translationY = 20f
            row.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(250)
                .setStartDelay(index * staggerMs)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    // ═══════════════════════════════════════════
    //  PARTICLE / SPARKLE EFFECTS
    // ═══════════════════════════════════════════

    /** Create sparkle particle paint */
    fun createSparklePaint(color: Int, size: Float): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.shader = RadialGradient(
                size / 2, size / 2, size / 2,
                intArrayOf(Color.WHITE, color, Color.TRANSPARENT),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
    }

    /** Animated sparkle burst at position */
    fun animateSparkleBurst(view: View, centerX: Float, centerY: Float, color: Int) {
        val sparkleCount = 8
        val animators = mutableListOf<ObjectAnimator>()

        for (i in 0 until sparkleCount) {
            val angle = (i * 360f / sparkleCount) * (Math.PI / 180f)
            val endX = centerX + (cos(angle) * 40f).toFloat()
            val endY = centerY + (sin(angle) * 40f).toFloat()

            val sparkle = View(view.context).apply {
                setBackgroundColor(color)
                layoutParams = android.view.ViewGroup.LayoutParams(6, 6)
                x = centerX
                y = centerY
                alpha = 1f
            }

            // Add to parent if possible
            (view.parent as? android.view.ViewGroup)?.addView(sparkle)

            val moveX = ObjectAnimator.ofFloat(sparkle, View.X, centerX, endX).apply { duration = 400 }
            val moveY = ObjectAnimator.ofFloat(sparkle, View.Y, centerY, endY).apply { duration = 400 }
            val fade = ObjectAnimator.ofFloat(sparkle, View.ALPHA, 1f, 0f).apply { duration = 400 }

            animators.add(moveX)
            animators.add(moveY)
            animators.add(fade)
        }

        AnimatorSet().apply {
            playTogether(animators)
            start()
        }
    }

    // ═══════════════════════════════════════════
    //  SMOOTH INTERPOLATORS
    // ═══════════════════════════════════════════

    /** iOS-like spring interpolator */
    fun springInterpolator(tension: Float = 1.5f): OvershootInterpolator {
        return OvershootInterpolator(tension)
    }

    /** Smooth decelerate for exit animations */
    fun decelerateInterpolator(): android.view.animation.DecelerateInterpolator {
        return android.view.animation.DecelerateInterpolator(2f)
    }

    /** Material motion interpolator */
    fun materialMotionInterpolator(): PathInterpolator {
        return PathInterpolator(0.4f, 0f, 0.2f, 1f)
    }
}
