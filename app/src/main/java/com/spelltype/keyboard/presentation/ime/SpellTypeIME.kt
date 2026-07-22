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
import com.spelltype.keyboard.domain.PreviewStyler
import com.spelltype.keyboard.domain.MoodDetector
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

    // Phase 5 & 6 Settings
    private var vibrationEnabled = true
    private var soundEnabled = true
    private var vibrationStrength = 50
    private var soundVolume = 50
    private var keyboardHeight = "MEDIUM"
    private var numberRowEnabled = true
    private var autoSuggestionsEnabled = true
    private var swipeTypingEnabled = false
    private var colorfulPreviewEnabled = true
    private var giantWordsEnabled = false
    private var themeSelection = "DARK"

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

    private val numKeyIds = listOf(
        R.id.num_1, R.id.num_2, R.id.num_3, R.id.num_4, R.id.num_5,
        R.id.num_6, R.id.num_7, R.id.num_8, R.id.num_9, R.id.num_0
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

    // Suggestions Style Targets
    private var suggestedStyleLeft: FrameStyle? = null
    private var suggestedStyleRight: FrameStyle? = null

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
        updateSuggestionsBar()
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

        // Map permanent number row keys
        for (id in numKeyIds) {
            val numView: TextView = keyboardView.findViewById(id)
            numView.setOnClickListener {
                onKeyClickFeedback(numView)
                handleKeyClick(numView.text.toString())
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

        // Setup suggestions click listeners
        keyboardView.findViewById<View>(R.id.suggestion_left).setOnClickListener {
            suggestedStyleLeft?.let { style ->
                onKeyClickFeedback(it)
                selectFrameStyle(style)
            }
        }
        keyboardView.findViewById<View>(R.id.suggestion_right).setOnClickListener {
            suggestedStyleRight?.let { style ->
                onKeyClickFeedback(it)
                selectFrameStyle(style)
            }
        }

        // Load dynamic settings flows
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
        serviceScope.launch {
            repository.getVibrationStrength().collect { strength ->
                vibrationStrength = strength
            }
        }
        serviceScope.launch {
            repository.getKeySoundVolume().collect { volume ->
                soundVolume = volume
            }
        }
        serviceScope.launch {
            repository.getKeyboardHeight().collect { height ->
                keyboardHeight = height
                applyKeyboardHeight(height)
            }
        }
        serviceScope.launch {
            repository.getNumberRowEnabled().collect { enabled ->
                numberRowEnabled = enabled
                keyboardRootView?.findViewById<View>(R.id.number_row)?.visibility = if (enabled) View.VISIBLE else View.GONE
            }
        }
        serviceScope.launch {
            repository.getAutoSuggestionsEnabled().collect { enabled ->
                autoSuggestionsEnabled = enabled
                keyboardRootView?.findViewById<View>(R.id.auto_suggestions_bar)?.visibility = if (enabled) View.VISIBLE else View.GONE
            }
        }
        serviceScope.launch {
            repository.getColorfulPreviewEnabled().collect { enabled ->
                colorfulPreviewEnabled = enabled
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getGiantWordsEnabled().collect { enabled ->
                giantWordsEnabled = enabled
                updateLivePreviewBar()
            }
        }
        serviceScope.launch {
            repository.getThemeSelection().collect { theme ->
                themeSelection = theme
                applyKeyboardTheme(theme)
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

            val isFav = favoriteStyles.contains(style.name)
            val isPrem = StyleCategorizer.isPremium(style)
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
            val vibrator = getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
            vibrator?.let {
                val duration = (vibrationStrength * 0.4).toLong().coerceAtLeast(1)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val amplitude = (vibrationStrength * 2.55).toInt().coerceIn(1, 255)
                    it.vibrate(android.os.VibrationEffect.createOneShot(duration, amplitude))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(duration)
                }
            }
        }
        if (soundEnabled) {
            val am = getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
            val vol = soundVolume / 100f
            am?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD, vol)
        }

        // 120fps smooth scale popup animation
        view.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .setDuration(60)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(60)
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
            updateSuggestionsBar()
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
            updateSuggestionsBar()
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
        updateSuggestionsBar()
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

            // Apply Rainbow Coloring and Giant Sizing preview style dynamically
            previewTextView.text = PreviewStyler.stylePreview(
                processed,
                colorfulPreviewEnabled,
                giantWordsEnabled
            )
        }
    }

    private fun updateSuggestionsBar() {
        val root = keyboardRootView ?: return
        val suggestionsBar = root.findViewById<View>(R.id.auto_suggestions_bar) ?: return
        if (!autoSuggestionsEnabled || composingText.isEmpty()) {
            suggestionsBar.visibility = if (autoSuggestionsEnabled) View.VISIBLE else View.GONE
            root.findViewById<TextView>(R.id.suggestion_left)?.text = "spell"
            root.findViewById<TextView>(R.id.suggestion_center)?.text = "SpellType"
            root.findViewById<TextView>(R.id.suggestion_right)?.text = "keyboard"
            suggestedStyleLeft = null
            suggestedStyleRight = null
            return
        }

        // Rule-based AI Mood detection in real-time as user types!
        val rawInput = composingText.toString()
        val moodSuggestion = MoodDetector.detectMood(rawInput)

        val leftText: TextView = root.findViewById(R.id.suggestion_left) ?: return
        val centerText: TextView = root.findViewById(R.id.suggestion_center) ?: return
        val rightText: TextView = root.findViewById(R.id.suggestion_right) ?: return

        centerText.text = "Mood: ${moodSuggestion.mood.displayName} ${moodSuggestion.mood.emoji}"

        val list = moodSuggestion.suggestedStyles
        if (list.isNotEmpty()) {
            suggestedStyleLeft = list[0]
            leftText.text = "Try " + list[0].name.lowercase().replace("_", " ")
        } else {
            suggestedStyleLeft = null
            leftText.text = "Normal"
        }

        if (list.size > 1) {
            suggestedStyleRight = list[1]
            rightText.text = "Try " + list[1].name.lowercase().replace("_", " ")
        } else {
            suggestedStyleRight = null
            rightText.text = "Star"
        }
    }

    private fun applyKeyboardHeight(heightSelection: String) {
        val container = keyboardRootView as? LinearLayout ?: return
        val density = resources.displayMetrics.density

        // Loop and adjust vertical row parameters
        for (i in 0 until container.childCount) {
            val row = container.getChildAt(i) as? LinearLayout ?: continue
            val rowId = row.id
            if (rowId == R.id.number_row || rowId == R.id.auto_suggestions_bar || row.childCount > 0) {
                if (rowId == R.id.auto_suggestions_bar) continue

                val lp = row.layoutParams as? LinearLayout.LayoutParams ?: continue
                lp.height = when (heightSelection) {
                    "SMALL" -> (44 * density).toInt()
                    "LARGE" -> (64 * density).toInt()
                    else -> (54 * density).toInt() // MEDIUM
                }
                row.layoutParams = lp
            }
        }
    }

    private fun applyKeyboardTheme(theme: String) {
        val root = keyboardRootView ?: return
        val isLight = theme == "LIGHT"

        val bgColor = when (theme) {
            "AMOLED" -> android.graphics.Color.BLACK
            "LIGHT" -> android.graphics.Color.parseColor("#F3F4F6")
            "BLUE" -> android.graphics.Color.parseColor("#1E3A8A")
            "PURPLE" -> android.graphics.Color.parseColor("#4C1D95")
            "GREEN" -> android.graphics.Color.parseColor("#064E3B")
            else -> android.graphics.Color.parseColor("#0B0F19") // DARK
        }

        val keyBgColor = when (theme) {
            "AMOLED" -> android.graphics.Color.parseColor("#111111")
            "LIGHT" -> android.graphics.Color.parseColor("#FFFFFF")
            "BLUE" -> android.graphics.Color.parseColor("#3B82F6")
            "PURPLE" -> android.graphics.Color.parseColor("#7C3AED")
            "GREEN" -> android.graphics.Color.parseColor("#10B981")
            else -> android.graphics.Color.parseColor("#1F2937") // DARK
        }

        val textColor = if (isLight) android.graphics.Color.parseColor("#1F2937") else android.graphics.Color.WHITE

        root.setBackgroundColor(bgColor)

        // Apply themes to keys
        for (id in letterKeyIds) {
            val key = keyViews[id] ?: continue
            key.setBackgroundColor(keyBgColor)
            key.setTextColor(textColor)
        }
        for (id in numKeyIds) {
            val key = root.findViewById<TextView>(id) ?: continue
            key.setBackgroundColor(keyBgColor)
            key.setTextColor(textColor)
        }

        val specialBgColor = if (isLight) android.graphics.Color.parseColor("#E5E7EB") else android.graphics.Color.parseColor("#111827")
        val specialIds = listOf(R.id.btn_shift, R.id.btn_backspace, R.id.btn_mode, R.id.btn_space)
        for (id in specialIds) {
            val btn = root.findViewById<View>(id) ?: continue
            btn.setBackgroundColor(specialBgColor)
            if (btn is TextView) {
                btn.setTextColor(textColor)
            }
        }
    }
}
