package com.salmanlaghari.spelltypekeyboard.domain.features

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import com.salmanlaghari.spelltypekeyboard.core.AppLog

/**
 * Real Settings Manager
 * Handles keyboard settings, input method selection, and system integration
 */
object SettingsManager {

    /**
     * Open keyboard settings in system. Returns false when the screen could not be opened.
     */
    fun openKeyboardSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            AppLog.e("SettingsManager.openKeyboardSettings", e)
            false
        } catch (e: SecurityException) {
            AppLog.e("SettingsManager.openKeyboardSettings", e)
            false
        }
    }

    /**
     * Open input method picker. Returns false when the picker is unavailable.
     */
    fun openInputMethodPicker(context: Context): Boolean {
        return try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (imm == null) {
                AppLog.e("SettingsManager.openInputMethodPicker", "InputMethodManager unavailable")
                return false
            }
            imm.showInputMethodPicker()
            true
        } catch (e: SecurityException) {
            AppLog.e("SettingsManager.openInputMethodPicker", e)
            false
        }
    }

    /**
     * Check if SpellType is the default keyboard
     */
    fun isDefaultKeyboard(context: Context): Boolean {
        return try {
            val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            defaultIme?.contains("spelltype", ignoreCase = true) == true
        } catch (e: SecurityException) {
            AppLog.e("SettingsManager.isDefaultKeyboard", e)
            false
        }
    }

    /**
     * Get current default keyboard name
     */
    fun getCurrentKeyboard(context: Context): String {
        return try {
            val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            defaultIme ?: "Unknown"
        } catch (e: SecurityException) {
            AppLog.e("SettingsManager.getCurrentKeyboard", e)
            "Unknown"
        }
    }
}
