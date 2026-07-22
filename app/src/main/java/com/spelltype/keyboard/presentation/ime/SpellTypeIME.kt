package com.spelltype.keyboard.presentation.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import com.spelltype.keyboard.R
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.data.db.SpellTypeDatabase
import com.spelltype.keyboard.data.repository.KeyboardRepositoryImpl
import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.ShapeEngine
import com.spelltype.keyboard.domain.StyleCategorizer
import com.spelltype.keyboard.domain.UnicodeStylingEngine
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import com.spelltype.keyboard.domain.usecase.*
import kotlinx.coroutines.*

class SpellTypeIME : InputMethodService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var repository: KeyboardRepository
    private lateinit var applyFrameUseCase: ApplyFrameUseCase
    private lateinit var saveSelectedFrameStyleUseCase: SaveSelectedFrameStyleUseCase
    private lateinit var getSelectedFrameStyleUseCase: GetSelectedFrameStyleUseCase

    private var activeStyle = FrameStyle.NONE
    private var activeShape = ShapeLayout.NONE
    private var activeUnicode = UnicodeStyle.NONE
    private var glitterEnabled = false
    private var customSignature = ""
    private var favoriteStyles = emptySet<String>()
    private var vibrationEnabled = true
    private var soundEnabled = true

    private var isShifted = false
    private var isSymbolMode = false

    private val composingText = StringBuilder()

    private val letterKeyIds = listOf(
        R.id.btn_q, R.id.btn_w, R.id.btn_e, R.id.btn_r, R.id.btn_t,
        R.id.btn_y, R.id.btn_u, R.id.btn_i, R.id.btn_o, R.id.btn_p,
        R.id.btn_a, R.id.btn_s, R.id.btn_d, R.id.btn_f, R.id.btn_g,
        R.id.btn_h, R.id.btn_j, R.id.btn_k, R.id.btn_l,
        R.id.btn_z, R.id.btn_x, R.id.btn_c, R.id.btn_v,
        R.id.btn_b, R.id.btn_n, R.id.btn_m
    )

    private val lettersLower = listOf(
        "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
        "a", "s", "d", "f", "g", "h", "j", "k", "l",
        "z", "x", "c", "v", "b", "n", "m"
    )

    private val lettersUpper = listOf(
        "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
        "A", "S", "D", "F", "G", "H", "J", "K", "L",
        "Z", "X", "C", "V", "B", "N", "M"
    )

    private val symbols = listOf(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
        "@", "#", "$", "%", "&", "-", "+", "(", ")",
        "*", "\"", "'", ":", ";", "!", "?"
    )

    private var keyViews = mutableMapOf<Int, TextView>()
    private var keyboardRootView: View? = null

    override fun onCreate() {
        super.onCreate()
        val database = SpellTypeDatabase.getDatabase(this)
        val preferences = KeyboardPreferences(this)
        repository = KeyboardRepositoryImpl(database.savedArtDao(), preferences)

        applyFrameUseCase = ApplyFrameUseCase(repository)
        saveSelectedFrameStyleUseCase = SaveSelectedFrameStyleUseCase(repository)
        getSelectedFrameStyleUseCase = GetSelectedFrameStyleUseCase(repository)
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        composingText.clear()
        updateLivePreviewBar()
    }

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        keyboardRootView = keyboardView

        // Map standard and special letter keys
        for (id in letterKeyIds) {
            val keyView: TextView = keyboardView.findViewById(id)
            keyViews[id] = keyView
            keyView.setOnClickListener {
                onKeyClickFeedback(keyView)
                handleKeyClick(keyView.text.toString())
            }
        }

        // Special keys
        val btnShift = keyboardView.findViewById<View>(R.id.btn_shift)
        btnShift.setOnClickListener {
            onKeyClickFeedback(btnShift)
            toggleShift()
        }

        val btnBackspace = keyboardView.findViewById<View>(R.id.btn_backspace)
        btnBackspace.setOnClickListener {
            onKeyClickFeedback(btnBackspace)
            handleBackspace()
        }

        val btnMode = keyboardView.findViewById<View>(R.id.btn_mode)
        btnMode.setOnClickListener {
            onKeyClickFeedback(btnMode)
            toggleMode()
        }

        val btnSpace = keyboardView.findViewById<View>(R.id.btn_space)
        btnSpace.setOnClickListener {
            onKeyClickFeedback(btnSpace)
            handleSpace()
        }

        val btnEnter = keyboardView.findViewById<View>(R.id.btn_enter)
        btnEnter.setOnClickListener {
            onKeyClickFeedback(btnEnter)
            handleEnter()
        }

        // Load dynamic settings flow
        serviceScope.launch {
            getSelectedFrameStyleUseCase().collect { style ->
                activeStyle = style
                updateChipHighlighting()
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getSelectedShapeLayout().collect { shape ->
                activeShape = shape
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getSelectedUnicodeStyle().collect { unicode ->
                activeUnicode = unicode
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getGlitterEnabled().collect { enabled ->
                glitterEnabled = enabled
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getCustomSignature().collect { signature ->
                customSignature = signature
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getFavoriteStyles().collect { favorites ->
                favoriteStyles = favorites
                val container: LinearLayout? = keyboardRootView?.findViewById(R.id.quick_art_container)
                if (container != null) {
                    populateQuickArtBar(container)
                }
            }
        }
        serviceScope.launch {
            repository.getVibrationEnabled().collect { enabled ->
                vibrationEnabled = enabled
            }
        }
        serviceScope.launch {
            repository.getSoundEnabled().collect { enabled ->
                soundEnabled = enabled
            }
        }

        // Programmatically populate Quick Art Bar container with 36+ chips
        val container: LinearLayout? = keyboardView.findViewById(R.id.quick_art_container)
        if (container != null) {
            populateQuickArtBar(container)
        }

        updateKeyLabels()
        return keyboardView
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun getSortedStyles(): List<FrameStyle> {
        val allStyles = FrameStyle.values().toList()
        return allStyles.sortedWith(compareBy(
            { it != FrameStyle.NONE },
            { !favoriteStyles.contains(it.name) }
        ))
    }

    private fun populateQuickArtBar(container: LinearLayout) {
        container.removeAllViews()
        val styles = getSortedStyles()
        for (style in styles) {
            val textView = TextView(this)

            // Add custom visual decorators for favorites or premiums
            val isFav = favoriteStyles.contains(style.name)
            val isPrem = com.spelltype.keyboard.domain.StyleCategorizer.isPremium(style)
            val prefix = when {
                isFav -> "♥ "
                isPrem -> "👑 "
                else -> ""
            }

            val name = if (style == FrameStyle.NONE) "Normal" else style.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
            textView.text = "$prefix$name"
            textView.setTextColor(resources.getColor(R.color.key_text_color, null))
            textView.textSize = 12f

            val density = resources.displayMetrics.density
            val padLR = (12 * density).toInt()
            val padTB = (6 * density).toInt()
            textView.setPadding(padLR, padTB, padLR, padTB)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val margin = (4 * density).toInt()
            params.setMargins(margin, 0, margin, 0)
            textView.layoutParams = params

            textView.isClickable = true
            textView.isFocusable = true
            textView.setBackgroundResource(
                if (activeStyle == style) R.drawable.chip_active_background
                else R.drawable.chip_inactive_background
            )

            textView.setOnClickListener {
                selectFrameStyle(style)
            }
            container.addView(textView)
        }
    }

    private fun onKeyClickFeedback(view: View) {
        if (vibrationEnabled) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        }
        if (soundEnabled) {
            val am = getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
            am?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD)
        }
        view.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .setDuration(70)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(70)
                    .start()
            }.start()
    }

    private fun selectFrameStyle(style: FrameStyle) {
        serviceScope.launch {
            saveSelectedFrameStyleUseCase(style)
            activeStyle = style
            updateChipHighlighting()
            updateLivePreviewBar()
        }
    }

    private fun updateChipHighlighting() {
        val container = keyboardRootView?.findViewById<LinearLayout>(R.id.quick_art_container) ?: return
        val styles = getSortedStyles()
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i) as? TextView ?: continue
            val style = styles.getOrNull(i) ?: continue
            child.setBackgroundResource(
                if (activeStyle == style) R.drawable.chip_active_background
                else R.drawable.chip_inactive_background
            )
        }
    }

    private fun isStylingActive(): Boolean {
        return activeStyle != FrameStyle.NONE ||
                activeShape != ShapeLayout.NONE ||
                activeUnicode != UnicodeStyle.NONE ||
                glitterEnabled
    }

    private fun handleKeyClick(text: String) {
        val ic: InputConnection = currentInputConnection ?: return
        if (!isStylingActive()) {
            ic.commitText(text, 1)
        } else {
            composingText.append(text)
            ic.setComposingText(composingText.toString(), 1)
            updateLivePreviewBar()
        }

        if (isShifted && !isSymbolMode) {
            isShifted = false
            updateKeyLabels()
        }
    }

    private fun toggleShift() {
        if (isSymbolMode) return
        isShifted = !isShifted
        updateKeyLabels()
    }

    private fun toggleMode() {
        isSymbolMode = !isSymbolMode
        isShifted = false
        updateKeyLabels()
    }

    private fun updateKeyLabels() {
        val modeButton: TextView? = keyboardRootView?.findViewById(R.id.btn_mode)
        modeButton?.text = if (isSymbolMode) "abc" else "?123"

        for (i in letterKeyIds.indices) {
            val id = letterKeyIds[i]
            val view = keyViews[id] ?: continue

            if (isSymbolMode) {
                if (i < symbols.size) {
                    view.text = symbols[i]
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.INVISIBLE
                }
            } else {
                view.visibility = View.VISIBLE
                view.text = if (isShifted) lettersUpper[i] else lettersLower[i]
            }
        }
    }

    private fun handleBackspace() {
        val ic: InputConnection = currentInputConnection ?: return
        if (isStylingActive() && composingText.isNotEmpty()) {
            composingText.deleteAt(composingText.length - 1)
            if (composingText.isEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.setComposingText(composingText.toString(), 1)
            }
            updateLivePreviewBar()
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun handleSpace() {
        val ic: InputConnection = currentInputConnection ?: return
        if (isStylingActive() && composingText.isNotEmpty()) {
            commitComposingText {
                ic.commitText(" ", 1)
            }
        } else {
            ic.commitText(" ", 1)
        }
    }

    private fun handleEnter() {
        val ic: InputConnection = currentInputConnection ?: return
        if (isStylingActive() && composingText.isNotEmpty()) {
            commitComposingText {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun commitComposingText(onComplete: (() -> Unit)? = null) {
        val textToFormat = composingText.toString()
        composingText.clear()
        updateLivePreviewBar()
        val ic: InputConnection = currentInputConnection ?: return

        serviceScope.launch {
            val styled = applyFrameUseCase(
                text = textToFormat,
                style = activeStyle,
                shape = activeShape,
                unicode = activeUnicode,
                glitterEnabled = glitterEnabled,
                signature = customSignature
            )
            ic.commitText(styled, 1)
            onComplete?.invoke()
        }
    }

    private fun updateLivePreviewBar() {
        val previewTextView = keyboardRootView?.findViewById<TextView>(R.id.tv_keyboard_live_preview) ?: return
        if (composingText.isEmpty()) {
            previewTextView.visibility = View.GONE
        } else {
            previewTextView.visibility = View.VISIBLE

            val textToFormat = composingText.toString()
            var processed = UnicodeStylingEngine.applyStyle(textToFormat, activeUnicode)

            if (glitterEnabled) {
                val glitterSymbols = listOf("✨", "🌟", "⭐", "💫")
                val words = processed.split(" ")
                val sb = StringBuilder()
                for (i in words.indices) {
                    sb.append(words[i])
                    if (i < words.size - 1) {
                        val symbol = glitterSymbols[i % glitterSymbols.size]
                        sb.append(" $symbol ")
                    }
                }
                processed = if (words.size == 1) "✨ $processed ✨" else sb.toString()
            }

            processed = ShapeEngine.applyShape(processed, activeShape)
            processed = ArtEngine.applyFrame(processed, activeStyle)
            if (customSignature.isNotEmpty()) {
                processed = "$processed\n$customSignature"
            }

            previewTextView.text = processed
        }
    }
}
