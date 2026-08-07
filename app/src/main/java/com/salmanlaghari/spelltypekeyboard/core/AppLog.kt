package com.salmanlaghari.spelltypekeyboard.core

import android.util.Log
import kotlinx.coroutines.CancellationException

/**
 * Central logging entry point for the app. Failures are reported under a single searchable
 * tag instead of bare stack traces on `System.err`.
 */
object AppLog {

    const val TAG = "SpellType"

    fun w(where: String, error: Throwable) {
        Log.w(TAG, where, error)
    }

    fun e(where: String, error: Throwable) {
        Log.e(TAG, where, error)
    }

    fun e(where: String, message: String) {
        Log.e(TAG, "$where: $message")
    }
}

/**
 * Rethrows [CancellationException] so a broad `catch (e: Exception)` inside a coroutine cannot
 * swallow cancellation and break structured concurrency.
 */
fun Throwable.rethrowIfCancellation() {
    if (this is CancellationException) throw this
}
