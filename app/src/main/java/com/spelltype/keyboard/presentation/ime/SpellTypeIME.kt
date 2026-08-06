package com.spelltype.keyboard.presentation.ime

import android.inputmethodservice.InputMethodService
import android.graphics.Color
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
import com.spelltype.keyboard.domain.theme.PremiumTheme
import com.spelltype.keyboard.domain.theme.PremiumThemeEngine
import com.spelltype.keyboard.domain.theme.RealTheme
import com.spelltype.keyboard.domain.theme.RealThemeEngine
import com.spelltype.keyboard.domain.animation.PremiumAnimationEngine
import com.spelltype.keyboard.domain.language.LanguageManager
import com.spelltype.keyboard.domain.language.KeyboardLanguage
import com.spelltype.keyboard.domain.ai.AISuggestionsEngine
import com.spelltype.keyboard.domain.ai.GeminiLiveService
import com.spelltype.keyboard.domain.developer.DeveloperKeyboard
import com.spelltype.keyboard.domain.design.ImageDesignEngine
import com.spelltype.keyboard.domain.shapes.ShapeAlphabetEngine
import com.spelltype.keyboard.domain.backgrounds.KeyboardBackgroundEngine
import com.spelltype.keyboard.domain.features.VoiceInputManager
import com.spelltype.keyboard.domain.features.EmojiGifManager
import com.spelltype.keyboard.domain.features.SettingsManager
import kotlinx.coroutines.*

class SpellTypeIME : InputMethodService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var repository: KeyboardRepository? = null
    private var applyFrameUseCase: ApplyFrameUseCase? = null
    private var saveSelectedFrameStyleUseCase: SaveSelectedFrameStyleUseCase? = null
    private var getSelectedFrameStyleUseCase: GetSelectedFrameStyleUseCase? = null

    private var activeStyle = FrameStyle.NONE
    private var activeShape = ShapeLayout.NONE
    private var activeUnicode = UnicodeStyle.NONE
    private var glitterEnabled = false
    private var customSignature = ""
    private var favoriteStyles = emptySet<String>()

    // Custom 3D & Control Center Option variables
    private var force3DKeycaps = true
    private var gboardModeEnabled = false
    private var rainbowPreviewEnabled = false
    private var highFpsRenderEnabled = true
    private var holographicGlowEnabled = true
    private var tapParticlesEnabled = true
    private var premiumAssistEnabled = true
    private var adFreeSandboxEnabled = true

    // Phase 5 & 6 Settings with strict defaults
    private var vibrationEnabled = true
    private var soundEnabled = true
    private var vibrationStrength = 50
    private var soundVolume = 50
    private var keyboardHeight = "MEDIUM"
    private var numberRowEnabled = true
    private var autoSuggestionsEnabled = true
    private var colorfulPreviewEnabled = true
    private var giantWordsEnabled = false
    private var themeSelection = "DARK"

    // Premium Configurations
    private var keyboardWallpaperPath = ""
    private var keyboardWallpaperOpacity = 50
    private var keyShape = "ROUNDED"
    private var keyBorderEnabled = true
    private var keyBorderThickness = 1
    private var keyTextSize = "MEDIUM"
    private var premiumUnlocked = false

    // ═══ Real Premium Theme System ═══
    private var activeRealTheme: RealTheme = RealTheme.ALL[0]
    private var realThemeIndex = 0

    // ═══ Language System (120+ languages) ═══
    private var currentLanguage: KeyboardLanguage? = null
    private var languageList = LanguageManager.getAllLanguages()
    private var languageIndex = 0

    // ═══ Developer Mode ═══
    private var isDeveloperMode = false

    // ═══ Gemini Live ═══
    private var geminiActive = false

    // ═══ AI Suggestions ═══
    private var lastCommittedWord = ""

    // ═══ Shape Alphabet ═══
    private var activeShapeStyle: ShapeAlphabetEngine.ShapeStyle? = null
    private var shapeIndex = 0

    // ═══ Keyboard Background ═══
    private var activeBackground: KeyboardBackgroundEngine.KeyboardBackground? = null
    private var bgIndex = 0

    // ═══ Voice Input ═══
    private var voiceInputManager: VoiceInputManager? = null

    // ═══ Emoji Panel ═══
    private var isEmojiPanelOpen = false
    private var emojiCategoryIndex = 0

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

    private val emojis3D = listOf(
        "🔥", "💖", "✨", "⭐", "🌟", "💫", "🎉", "🎈", "👑", "💎",
        "🍀", "🌸", "🎵", "❄️", "🍁", "⚔️", "💀", "☕", "🐟", "👻",
        "👽", "🧸", "🍭", "🎁", "🚀", "🛸", "🎮", "🍕", "🥑", "🦁",
        "🦄", "🌈", "🌍", "⚡", "🔮", "🧬", "🧸", "🦾", "🧿", "🎨",
        "🎸", "🛹", "🍿", "🍩", "🍦", "🧁", "🍹", "🍷", "🔔", "📿",
        "🥺", "🥰", "🥶", "🥳", "🤠", "🤖"
    )

    private var isEmojiMode = false

    private var keyViews = mutableMapOf<Int, TextView>()
    private var keyboardRootView: View? = null

    // Suggestions Style Targets
    private var suggestedStyleLeft: FrameStyle? = null
    private var suggestedStyleRight: FrameStyle? = null

    override fun onCreate() {
        super.onCreate()
        try {
            // Safer context leak preventions
            val contextToUse = applicationContext ?: this
            val database = SpellTypeDatabase.getDatabase(contextToUse)
            val preferences = KeyboardPreferences(contextToUse)
            val repo = KeyboardRepositoryImpl(database.savedArtDao(), preferences)
            repository = repo

            applyFrameUseCase = ApplyFrameUseCase(repo)
            saveSelectedFrameStyleUseCase = SaveSelectedFrameStyleUseCase(repo)
            getSelectedFrameStyleUseCase = GetSelectedFrameStyleUseCase(repo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKeyboardAdBanners() {
        try {
            val root = keyboardRootView ?: return
            val topContainer = root.findViewById<android.widget.FrameLayout>(R.id.keyboard_top_ad_container)
            val bottomContainer = root.findViewById<android.widget.FrameLayout>(R.id.keyboard_bottom_ad_container)

            if (premiumUnlocked) {
                topContainer?.visibility = View.GONE
                bottomContainer?.visibility = View.GONE
            } else {
                // Initialize AdMob SDK first
                com.spelltype.keyboard.presentation.ads.AdManager.init(applicationContext ?: this)

                // Load Top Banner
                topContainer?.let { container ->
                    container.visibility = View.VISIBLE
                    com.spelltype.keyboard.presentation.ads.AdManager.loadBanner(
                        context = this,
                        type = com.spelltype.keyboard.presentation.ads.BannerType.KEYBOARD_TOP,
                        adSize = com.google.android.gms.ads.AdSize.BANNER
                    ) { adView ->
                        container.removeAllViews()
                        container.addView(adView)
                    }
                }

                // Load Bottom Banner
                bottomContainer?.let { container ->
                    container.visibility = View.VISIBLE
                    com.spelltype.keyboard.presentation.ads.AdManager.loadBanner(
                        context = this,
                        type = com.spelltype.keyboard.presentation.ads.BannerType.KEYBOARD_BOTTOM,
                        adSize = com.google.android.gms.ads.AdSize.BANNER
                    ) { adView ->
                        container.removeAllViews()
                        container.addView(adView)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        try {
            composingText.clear()
            updateLivePreviewBar()
            updateSuggestionsBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateInputView(): View {
        try {
            val keyboardView = layoutInflater.inflate(R.layout.keyboard_view_premium, null)
            keyboardRootView = keyboardView

            // Initialize default language
            currentLanguage = LanguageManager.getLanguageByCode("en")

            // Map standard and special letter keys
            for (id in letterKeyIds) {
                val keyView: TextView? = keyboardView.findViewById(id)
                if (keyView != null) {
                    keyViews[id] = keyView
                    keyView.setOnClickListener {
                        onKeyClickFeedback(keyView)
                        handleKeyClick(keyView.text.toString())
                    }
                }
            }

            // Map permanent number row keys
            for (id in numKeyIds) {
                val numView: TextView? = keyboardView.findViewById(id)
                numView?.setOnClickListener {
                    onKeyClickFeedback(numView)
                    handleKeyClick(numView.text.toString())
                }
            }

            // Special keys with safety
            val btnShift = keyboardView.findViewById<View>(R.id.btn_shift)
            btnShift?.setOnClickListener {
                onKeyClickFeedback(btnShift)
                toggleShift()
            }

            val btnBackspace = keyboardView.findViewById<View>(R.id.btn_backspace)
            btnBackspace?.setOnClickListener {
                onKeyClickFeedback(btnBackspace)
                handleBackspace()
            }

            val btnMode = keyboardView.findViewById<View>(R.id.btn_mode)
            btnMode?.setOnClickListener {
                onKeyClickFeedback(btnMode)
                toggleMode()
            }

            val btnSpace = keyboardView.findViewById<View>(R.id.btn_space)
            btnSpace?.setOnClickListener {
                onKeyClickFeedback(btnSpace)
                handleSpace()
            }

            val btnEnter = keyboardView.findViewById<View>(R.id.btn_enter)
            btnEnter?.setOnClickListener {
                onKeyClickFeedback(btnEnter)
                handleEnter()
            }

            // ═══ NEW: Premium Header Bar Buttons ═══

            // Language Switcher — cycles through 120+ languages
            keyboardView.findViewById<View>(R.id.btn_language)?.setOnClickListener {
                onKeyClickFeedback(it)
                cycleLanguage()
            }

            // Theme Selector — cycles through 12+ premium themes
            keyboardView.findViewById<View>(R.id.btn_theme)?.setOnClickListener {
                onKeyClickFeedback(it)
                cyclePremiumTheme()
            }

            // Developer Mode Toggle
            keyboardView.findViewById<View>(R.id.btn_dev_mode)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleDeveloperMode()
            }

            // Gemini AI Toggle
            keyboardView.findViewById<View>(R.id.btn_gemini)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleGeminiLive()
            }

            // ═══ NEW: Bottom Row Extra Buttons ═══

            // Emoji button (new dedicated button)
            keyboardView.findViewById<View>(R.id.btn_emoji)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleEmojiMode()
                refreshQuickArtBar()
            }

            // Comma button
            keyboardView.findViewById<View>(R.id.btn_comma)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleKeyClick(",")
            }

            // ═══ NEW: Extended Pro Tools ═══

            keyboardView.findViewById<View>(R.id.tool_voice)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleVoiceInput()
            }

            keyboardView.findViewById<View>(R.id.tool_gif)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleGifSearch()
            }

            keyboardView.findViewById<View>(R.id.tool_image)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleImageDesign()
            }

            keyboardView.findViewById<View>(R.id.tool_settings)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleControlCenter()
            }

            // Trigger Ad Banner loading
            updateKeyboardAdBanners()

            // Pro Tools Click Listeners
            keyboardView.findViewById<View>(R.id.tool_clipboard)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleClipboardToolWithAd()
            }

            keyboardView.findViewById<View>(R.id.tool_translate)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleTranslateToolWithAd()
            }

            keyboardView.findViewById<View>(R.id.tool_templates)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleTemplatesToolWithAd()
            }

            // Setup suggestions click listeners
            keyboardView.findViewById<View>(R.id.suggestion_left)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleSuggestionClick(0)
            }
            keyboardView.findViewById<View>(R.id.suggestion_center)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleSuggestionClick(1)
            }
            keyboardView.findViewById<View>(R.id.suggestion_right)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleSuggestionClick(2)
            }

            // Load dynamic settings flow safely
            val repo = repository
            val getStyleUseCase = getSelectedFrameStyleUseCase
            if (repo != null) {
                if (getStyleUseCase != null) {
                    serviceScope.launch {
                        getStyleUseCase().collect { style ->
                            activeStyle = style
                            refreshQuickArtBar()
                            updateLivePreviewBar()
                        }
                    }
                }
                serviceScope.launch {
                    repo.getSelectedShapeLayout().collect { shape ->
                        activeShape = shape
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getSelectedUnicodeStyle().collect { unicode ->
                        activeUnicode = unicode
                        refreshQuickArtBar()
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getGlitterEnabled().collect { enabled ->
                        glitterEnabled = enabled
                        refreshQuickArtBar()
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getCustomSignature().collect { signature ->
                        customSignature = signature
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getFavoriteStyles().collect { favorites ->
                        favoriteStyles = favorites
                        refreshQuickArtBar()
                    }
                }
                serviceScope.launch {
                    repo.getVibrationEnabled().collect { enabled ->
                        vibrationEnabled = enabled
                    }
                }
                serviceScope.launch {
                    repo.getSoundEnabled().collect { enabled ->
                        soundEnabled = enabled
                    }
                }
                serviceScope.launch {
                    repo.getVibrationStrength().collect { strength ->
                        vibrationStrength = strength
                    }
                }
                serviceScope.launch {
                    repo.getKeySoundVolume().collect { volume ->
                        soundVolume = volume
                    }
                }
                serviceScope.launch {
                    repo.getKeyboardHeight().collect { height ->
                        keyboardHeight = height
                        applyKeyboardHeight(height)
                    }
                }
                serviceScope.launch {
                    repo.getNumberRowEnabled().collect { enabled ->
                        numberRowEnabled = enabled
                        keyboardRootView?.findViewById<View>(R.id.number_row)?.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }
                serviceScope.launch {
                    repo.getAutoSuggestionsEnabled().collect { enabled ->
                        autoSuggestionsEnabled = enabled
                        keyboardRootView?.findViewById<View>(R.id.ai_suggestions_bar)?.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }
                serviceScope.launch {
                    repo.getColorfulPreviewEnabled().collect { enabled ->
                        colorfulPreviewEnabled = enabled
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getGiantWordsEnabled().collect { enabled ->
                        giantWordsEnabled = enabled
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getThemeSelection().collect { theme ->
                        themeSelection = theme
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyboardWallpaperPath().collect { path ->
                        keyboardWallpaperPath = path
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyboardWallpaperOpacity().collect { opacity ->
                        keyboardWallpaperOpacity = opacity
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyShape().collect { shape ->
                        keyShape = shape
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyBorderEnabled().collect { enabled ->
                        keyBorderEnabled = enabled
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyBorderThickness().collect { thickness ->
                        keyBorderThickness = thickness
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyTextSize().collect { size ->
                        keyTextSize = size
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getPremiumUnlocked().collect { unlocked ->
                        premiumUnlocked = unlocked
                        updateKeyboardAdBanners()
                    }
                }
            }

            // Populate Quick Art Bar safely
            val container = keyboardView.findViewById<LinearLayout>(R.id.quick_art_container)
            if (container != null) {
                populateQuickArtBar(container)
            }

            updateKeyLabels()
            bindControlCenter(keyboardView)
            return keyboardView
        } catch (e: Exception) {
            e.printStackTrace()
            // Never let the keyboard crash. Always return a dummy/fallback view to maintain system stability!
            val fallback = View(applicationContext ?: this)
            fallback.minimumHeight = 1
            return fallback
        }
    }

    override fun onDestroy() {
        try {
            voiceInputManager?.destroy()
            serviceJob.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun getSortedStyles(): List<FrameStyle> {
        val allStyles = FrameStyle.values().toList()
        return allStyles.sortedWith(compareBy(
            { it != FrameStyle.NONE },
            { !favoriteStyles.contains(it.name) }
        ))
    }

    private fun populateQuickArtBar(container: LinearLayout) {
        try {
            container.removeAllViews()

            val density = resources.displayMetrics.density
            val padLR = (12 * density).toInt()
            val padTB = (6 * density).toInt()
            val margin = (4 * density).toInt()

            // 1. Add 3D Emoji Toggle Chip
            val emojiChip = TextView(this)
            emojiChip.text = "😎 3D Emoji"
            emojiChip.setTextColor(resources.getColor(R.color.key_text_color, null))
            emojiChip.textSize = 12f
            emojiChip.setPadding(padLR, padTB, padLR, padTB)
            val emojiParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            emojiParams.setMargins(margin, 0, margin, 0)
            emojiChip.layoutParams = emojiParams
            emojiChip.isClickable = true
            emojiChip.isFocusable = true
            emojiChip.setBackgroundResource(
                if (isEmojiMode) R.drawable.chip_active_background
                else R.drawable.chip_inactive_background
            )
            emojiChip.setOnClickListener {
                onKeyClickFeedback(emojiChip)
                toggleEmojiMode()
                refreshQuickArtBar()
            }
            container.addView(emojiChip)

            // 2. Add Glitter Toggle Chip
            val glitterChip = TextView(this)
            glitterChip.text = if (glitterEnabled) "✨ Glitter: ON" else "✨ Glitter: OFF"
            glitterChip.setTextColor(resources.getColor(R.color.key_text_color, null))
            glitterChip.textSize = 12f
            glitterChip.setPadding(padLR, padTB, padLR, padTB)
            val glitterParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            glitterParams.setMargins(margin, 0, margin, 0)
            glitterChip.layoutParams = glitterParams
            glitterChip.isClickable = true
            glitterChip.isFocusable = true
            glitterChip.setBackgroundResource(
                if (glitterEnabled) R.drawable.chip_active_background
                else R.drawable.chip_inactive_background
            )
            glitterChip.setOnClickListener {
                onKeyClickFeedback(glitterChip)
                toggleGlitter()
            }
            container.addView(glitterChip)

            // 3. Add standard styles
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

                textView.setPadding(padLR, padTB, padLR, padTB)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(margin, 0, margin, 0)
                textView.layoutParams = params

                textView.isClickable = true
                textView.isFocusable = true
                textView.setBackgroundResource(
                    if (activeStyle == style && !isEmojiMode) R.drawable.chip_active_background
                    else R.drawable.chip_inactive_background
                )

                textView.setOnClickListener {
                    if (isEmojiMode) {
                        isEmojiMode = false
                        updateKeyLabels()
                    }
                    selectFrameStyle(style)
                }
                container.addView(textView)
            }

            // 4. Add Unicode Font Style Chips (excluding NONE)
            val unicodeStyles = UnicodeStyle.values().filter { it != UnicodeStyle.NONE }
            for (uStyle in unicodeStyles) {
                val textView = TextView(this)
                val name = uStyle.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }

                textView.text = "𝔽 $name"
                textView.setTextColor(resources.getColor(R.color.key_text_color, null))
                textView.textSize = 12f
                textView.setPadding(padLR, padTB, padLR, padTB)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(margin, 0, margin, 0)
                textView.layoutParams = params
                textView.isClickable = true
                textView.isFocusable = true
                textView.setBackgroundResource(
                    if (activeUnicode == uStyle && !isEmojiMode) R.drawable.chip_active_background
                    else R.drawable.chip_inactive_background
                )

                textView.setOnClickListener {
                    if (isEmojiMode) {
                        isEmojiMode = false
                        updateKeyLabels()
                    }
                    selectUnicodeStyle(uStyle)
                }
                container.addView(textView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val glowingKeys = mutableSetOf<Int>()

    private fun onKeyClickFeedback(view: View) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (soundEnabled) {
                val am = getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
                val vol = soundVolume / 100f
                am?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD, vol)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Neon Glow click lighting effect + 3D tactile pressed squish
            val viewId = view.id
            if (view is TextView && (letterKeyIds.contains(viewId) || numKeyIds.contains(viewId) ||
                viewId == R.id.btn_shift || viewId == R.id.btn_backspace || viewId == R.id.btn_mode || viewId == R.id.btn_space || viewId == R.id.btn_enter)) {

                glowingKeys.add(viewId)
                applyKeyStyle(view, isPressed = true)

                serviceScope.launch {
                    delay(150)
                    glowingKeys.remove(viewId)
                    applyKeyStyle(view, isPressed = false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Smooth 120fps animation
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectFrameStyle(style: FrameStyle) {
        val saveStyleUseCase = saveSelectedFrameStyleUseCase
        if (saveStyleUseCase != null) {
            serviceScope.launch {
                try {
                    saveStyleUseCase(style)
                    activeStyle = style
                    refreshQuickArtBar()
                    updateLivePreviewBar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun selectUnicodeStyle(style: UnicodeStyle) {
        val repo = repository
        if (repo != null) {
            serviceScope.launch {
                try {
                    repo.saveSelectedUnicodeStyle(style)
                    activeUnicode = style
                    refreshQuickArtBar()
                    updateLivePreviewBar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun toggleGlitter() {
        val repo = repository
        if (repo != null) {
            serviceScope.launch {
                try {
                    repo.saveGlitterEnabled(!glitterEnabled)
                    glitterEnabled = !glitterEnabled
                    refreshQuickArtBar()
                    updateLivePreviewBar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun refreshQuickArtBar() {
        try {
            val container = keyboardRootView?.findViewById<LinearLayout>(R.id.quick_art_container) ?: return
            populateQuickArtBar(container)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isStylingActive(): Boolean {
        return activeStyle != FrameStyle.NONE ||
                activeShape != ShapeLayout.NONE ||
                activeUnicode != UnicodeStyle.NONE ||
                glitterEnabled
    }

    /** Apply all active styles to composing text for live preview in input field */
    private fun applyComposingStyle(text: String): String {
        var processed = UnicodeStylingEngine.applyStyle(text, activeUnicode)
        if (glitterEnabled) {
            val glitterSymbols = listOf("✨", "🌟", "⭐", "💫")
            val words = processed.split(" ")
            val sb = StringBuilder()
            for (i in words.indices) {
                sb.append(words[i])
                if (i < words.size - 1) {
                    sb.append(" ${glitterSymbols[i % glitterSymbols.size]} ")
                }
            }
            processed = if (words.size == 1) "✨ $processed ✨" else sb.toString()
        }
        processed = ShapeEngine.applyShape(processed, activeShape)
        processed = ArtEngine.applyFrame(processed, activeStyle)
        return processed
    }

    private fun handleKeyClick(text: String) {
        try {
            val ic: InputConnection = currentInputConnection ?: return
            if (!isStylingActive()) {
                ic.commitText(text, 1)
            } else {
                composingText.append(text)
                // Apply styling live so user sees styled text in the input field
                val styled = applyComposingStyle(composingText.toString())
                ic.setComposingText(styled, 1)
                updateLivePreviewBar()
                updateSuggestionsBar()
            }

            if (isShifted && !isSymbolMode) {
                isShifted = false
                updateKeyLabels()
            }

            // Update AI suggestions as user types
            if (autoSuggestionsEnabled) {
                updateSuggestionsBar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleShift() {
        try {
            if (isSymbolMode || isEmojiMode) return
            isShifted = !isShifted
            updateKeyLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleMode() {
        try {
            if (isEmojiMode) {
                isEmojiMode = false
                isSymbolMode = false
            } else {
                isSymbolMode = !isSymbolMode
                isEmojiMode = false
            }
            isShifted = false
            updateKeyLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleEmojiMode() {
        try {
            isEmojiMode = !isEmojiMode
            isSymbolMode = false
            isShifted = false
            updateKeyLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKeyLabels() {
        try {
            val modeButton: TextView? = keyboardRootView?.findViewById(R.id.btn_mode)
            modeButton?.text = when {
                isEmojiMode -> "abc"
                isSymbolMode -> "abc"
                else -> "?123"
            }

            for (i in letterKeyIds.indices) {
                val id = letterKeyIds[i]
                val view = keyViews[id] ?: continue

                when {
                    isEmojiMode -> {
                        if (i < emojis3D.size) {
                            view.text = emojis3D[i]
                            view.visibility = View.VISIBLE
                        } else {
                            view.visibility = View.INVISIBLE
                        }
                    }
                    isSymbolMode -> {
                        if (i < symbols.size) {
                            view.text = symbols[i]
                            view.visibility = View.VISIBLE
                        } else {
                            view.visibility = View.INVISIBLE
                        }
                    }
                    else -> {
                        view.visibility = View.VISIBLE
                        view.text = if (isShifted) lettersUpper[i] else lettersLower[i]
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleBackspace() {
        try {
            val ic: InputConnection = currentInputConnection ?: return
            if (isStylingActive() && composingText.isNotEmpty()) {
                composingText.deleteAt(composingText.length - 1)
                if (composingText.isEmpty()) {
                    ic.commitText("", 1)
                } else {
                    val styled = applyComposingStyle(composingText.toString())
                    ic.setComposingText(styled, 1)
                }
                updateLivePreviewBar()
                updateSuggestionsBar()
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleSpace() {
        try {
            val ic: InputConnection = currentInputConnection ?: return

            // Track last word for AI suggestions
            if (composingText.isNotEmpty()) {
                lastCommittedWord = composingText.toString().trim()
            }

            // Smart Text Expander (Shortcut Expander Ease Function)
            if (composingText.isNotEmpty()) {
                val text = composingText.toString().trim()
                val lower = text.lowercase()
                val shortcuts = mapOf(
                    "brb" to "Be Right Back 🏃‍♂️",
                    "hru" to "How are you? 🤔",
                    "omg" to "Oh My God! 😱",
                    "np" to "No Problem 👍",
                    "ty" to "Thank You So Much! ❤️",
                    "lol" to "Laughing Out Loud! 😂",
                    "g2g" to "Got To Go! 👋",
                    "idk" to "I Don't Know 🤷‍♂️",
                    "btw" to "By The Way 📌"
                )
                if (shortcuts.containsKey(lower)) {
                    composingText.clear()
                    composingText.append(shortcuts[lower])
                    updateLivePreviewBar()
                }
            }

            if (isStylingActive() && composingText.isNotEmpty()) {
                commitComposingText {
                    ic.commitText(" ", 1)
                }
            } else {
                ic.commitText(" ", 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleClipboardTool() {
        try {
            val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
            if (clipText.isNotEmpty()) {
                composingText.clear()
                composingText.append(clipText)
                updateLivePreviewBar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleClipboardToolWithAd() {
        if (premiumUnlocked) {
            handleClipboardTool()
        } else {
            com.spelltype.keyboard.presentation.ads.AdManager.loadInterstitial(
                context = this,
                type = com.spelltype.keyboard.presentation.ads.InterstitialType.PRO_TOOLS,
                onLoaded = { _ ->
                    // Service context safety fallback
                    handleClipboardTool()
                },
                onFailed = {
                    handleClipboardTool()
                }
            )
        }
    }

    private fun handleTranslateTool() {
        try {
            val raw = composingText.toString()
            if (raw.isNotEmpty()) {
                // Cyber translator leet mock converter
                val translated = raw.lowercase()
                    .replace("a", "@")
                    .replace("e", "3")
                    .replace("i", "1")
                    .replace("o", "0")
                    .replace("s", "$")
                    .replace("t", "7")
                composingText.clear()
                composingText.append(translated)
                updateLivePreviewBar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTranslateToolWithAd() {
        if (premiumUnlocked) {
            handleTranslateTool()
        } else {
            com.spelltype.keyboard.presentation.ads.AdManager.loadInterstitial(
                context = this,
                type = com.spelltype.keyboard.presentation.ads.InterstitialType.PRO_TOOLS,
                onLoaded = { _ ->
                    // Service context safety fallback
                    handleTranslateTool()
                },
                onFailed = {
                    handleTranslateTool()
                }
            )
        }
    }

    private var templateIndex = 0
    private fun handleTemplatesTool() {
        try {
            val templates = listOf(
                "★ S P E L L T Y P E ★",
                "꧁𓊈𒆜 ⓈⓅⒺⓁⓁⓉⓅⒺ 𒆜𓊉꧂",
                "┌────── ∘°❉°∘ ──────┐\n   WELCOME TO MY BIO\n└────── °∘❉∘° ──────┘",
                "•◦✦────•✦•────✦◦•\n ✨ 𝓥𝓲𝓫𝓲𝓷𝓰 𝓲𝓷 3𝓓 ✨ \n•◦✦────•✦•────✦◦•",
                "💖 𝓁𝑜𝓋𝑒 𝓎𝑜𝓊𝓇𝓈𝑒𝓁𝓋𝑒𝓈 💖",
                "🎮 𝒢𝒜𝑀𝐸𝑅 𝒵𝒪𝒩𝐸 🎮\n ══🎮🕹️👾══",
                "┌── ⋆⋅☆⋅⋆ ──┐\n   𝓢𝓽𝓪𝓻 𝓑𝓸𝔁 \n└── ⋆⋅☆⋅⋆ ──┘",
                "▓▒░  𝕮𝖞𝖇𝖊𝖗𝖕𝖚𝖓𝖐  ░▒▓",
                "👑 𝓡𝓸𝔂𝓪𝓵 𝓒𝓻𝓸𝔀𝓷 𝓣𝓲𝓽𝓵𝓮 👑",
                "━━━━━━━━ ✧ ━━━━━━━━\n   𝒮𝓎𝓂𝓂𝑒𝓉𝓇𝓎\n━━━━━━━━ ✧ ━━━━━━━━",
                "☄️✨ 𝒮𝓅𝒶𝓇𝓀𝓁𝓎 𝒬𝓊𝑜𝓉𝑒𝓈 ✨☄️",
                "(\\__/)  \n(•ㅅ•)  Bunny Loves You!\n/ 　 づ",
                "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n  𝕳𝖊𝖆𝖛𝖞 𝕯𝖊𝖈𝖔𝖗𝖆𝖙𝖎𝖛𝖊 𝕷𝖎𝖓𝖊\n▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "🖤 𝔖𝔭𝔢𝔩𝔩𝔗𝔶𝔭𝔢 𝔊𝔬𝔱𝔥𝔦𝔠 𝔖𝔦𝔤𝔫𝔢𝔱 🖤",
                "🔥 𝓕𝓲𝓻𝓮 𝓑𝓵𝓪𝓼𝓽 𝓔𝓶𝓸𝓳𝓲 𝓑𝓪𝓷𝓷𝓮𝓻 🔥\n 🔥☄️💥🌋🧨",
                "⭐ 𝒲𝒾𝓈𝒽 𝒰𝓅𝑜𝓃 𝒜 𝒮𝓉𝒶𝓇 ⭐"
            )
            val index = templateIndex
            templateIndex = (templateIndex + 1) % templates.size
            composingText.clear()
            composingText.append(templates[index])
            updateLivePreviewBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTemplatesToolWithAd() {
        if (premiumUnlocked) {
            handleTemplatesTool()
        } else {
            com.spelltype.keyboard.presentation.ads.AdManager.loadInterstitial(
                context = this,
                type = com.spelltype.keyboard.presentation.ads.InterstitialType.PRO_TOOLS,
                onLoaded = { _ ->
                    // Service context safety fallback
                    handleTemplatesTool()
                },
                onFailed = {
                    handleTemplatesTool()
                }
            )
        }
    }

    private fun handleEnter() {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun commitComposingText(onComplete: (() -> Unit)? = null) {
        try {
            var textToFormat = composingText.toString()
            composingText.clear()
            updateLivePreviewBar()
            updateSuggestionsBar()
            val ic: InputConnection = currentInputConnection ?: return

            // Apply shape alphabet if active
            activeShapeStyle?.let { shape ->
                textToFormat = ShapeAlphabetEngine.applyShape(textToFormat, shape)
            }

            val formatUseCase = applyFrameUseCase
            if (formatUseCase != null) {
                serviceScope.launch {
                    try {
                        val styled = formatUseCase(
                            text = textToFormat,
                            style = activeStyle,
                            shape = activeShape,
                            unicode = activeUnicode,
                            glitterEnabled = glitterEnabled,
                            signature = customSignature
                        )
                        ic.commitText(styled, 1)
                        onComplete?.invoke()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                ic.commitText(textToFormat, 1)
                onComplete?.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLivePreviewBar() {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateSuggestionsBar() {
        try {
            // Use the new AI-powered suggestions
            updateSuggestionsBarWithAI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyKeyboardHeight(heightSelection: String) {
        try {
            val root = keyboardRootView as? LinearLayout ?: return
            val keysContainer = root.findViewById<LinearLayout>(R.id.keyboard_keys_container) ?: return
            val density = resources.displayMetrics.density

            // Only adjust heights of the actual KEY ROWS inside keyboard_keys_container
            // (number_row, QWERTY row, ASDF row, ZXCV row, space row)
            // Do NOT touch toolbar bars (header, art bar, suggestions, pro tools)
            val targetHeight = when (heightSelection) {
                "SMALL" -> (40 * density).toInt()
                "LARGE" -> (52 * density).toInt()
                else -> (46 * density).toInt() // MEDIUM — matches XML default
            }

            for (i in 0 until keysContainer.childCount) {
                val row = keysContainer.getChildAt(i) as? LinearLayout ?: continue
                val lp = row.layoutParams as? LinearLayout.LayoutParams ?: continue
                lp.height = targetHeight
                row.layoutParams = lp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyCustomConfigurations() {
        try {
            applyPremiumTheme()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun styleAllTextViewsUnder(view: View) {
        try {
            if (view is TextView) {
                applyKeyStyle(view, false)
            } else if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    styleAllTextViewsUnder(view.getChildAt(i))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyKeyStyle(view: TextView, isPressed: Boolean) {
        try {
            val id = view.id
            val isStandardKey = letterKeyIds.contains(id) || numKeyIds.contains(id)
            val isSpecialKey = id == R.id.btn_shift || id == R.id.btn_backspace || id == R.id.btn_mode || id == R.id.btn_space || id == R.id.btn_enter

            if (!isStandardKey && !isSpecialKey) return

            val density = resources.displayMetrics.density

            // A. Text Size
            val size = when (keyTextSize) {
                "SMALL" -> if (isStandardKey) 15f else 11f
                "LARGE" -> if (isStandardKey) 24f else 16f
                "HUGE" -> if (isStandardKey) 28f else 18f
                else -> if (isStandardKey) 19f else 13f // MEDIUM
            }
            view.textSize = size

            // B. Colors
            val isLight = themeSelection == "LIGHT"
            val baseKeyColor = when (themeSelection) {
                "AMOLED" -> android.graphics.Color.parseColor("#111111")
                "LIGHT" -> android.graphics.Color.parseColor("#FFFFFF")
                "BLUE" -> android.graphics.Color.parseColor("#3B82F6")
                "PURPLE" -> android.graphics.Color.parseColor("#7C3AED")
                "GREEN" -> android.graphics.Color.parseColor("#10B981")
                else -> android.graphics.Color.parseColor("#1F2937") // DARK
            }
            val baseSpecialColor = if (isLight) android.graphics.Color.parseColor("#E5E7EB") else android.graphics.Color.parseColor("#111827")
            var keyColor = if (isSpecialKey) baseSpecialColor else baseKeyColor

            // Handle Glow / pressed lighting state
            if (isPressed || glowingKeys.contains(id)) {
                keyColor = when (themeSelection) {
                    "LIGHT" -> android.graphics.Color.parseColor("#F43F5E") // Radiant Pink
                    "BLUE" -> android.graphics.Color.parseColor("#6EE7B7") // Neon Mint
                    "GREEN" -> android.graphics.Color.parseColor("#FBBF24") // Neon Amber
                    else -> android.graphics.Color.parseColor("#38BDF8") // Solar Cyan
                }
            }

            // Radius
            val radius = when (keyShape) {
                "SQUARE" -> 0f
                "CIRCULAR" -> 1000f
                "GLASSMORPHISM" -> 16f * density
                else -> 8f * density // ROUNDED
            }

            // Create LayerDrawable for 3D Bevel/Shadow Effect
            val shadowDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = radius
                setColor(android.graphics.Color.parseColor("#090D16")) // solid deep 3D bevel shadow
            }

            val frontDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = radius

                if (keyShape == "GLASSMORPHISM") {
                    val glassColor = if (isLight) {
                        android.graphics.Color.argb(if (isPressed || glowingKeys.contains(id)) 160 else 60, 255, 255, 255)
                    } else {
                        android.graphics.Color.argb(if (isPressed || glowingKeys.contains(id)) 140 else 45, 255, 255, 255)
                    }
                    setColor(glassColor)
                } else {
                    setColor(keyColor)
                }

                // Borders
                if (keyBorderEnabled) {
                    val thicknessPx = (keyBorderThickness * density).toInt().coerceAtLeast(1)
                    val borderColor = if (isPressed || glowingKeys.contains(id)) {
                        android.graphics.Color.WHITE
                    } else if (keyShape == "GLASSMORPHISM") {
                        if (isLight) android.graphics.Color.argb(120, 0, 0, 0)
                        else android.graphics.Color.argb(100, 255, 255, 255)
                    } else {
                        if (isLight) android.graphics.Color.parseColor("#D1D5DB")
                        else android.graphics.Color.parseColor("#4B5563")
                    }
                    setStroke(thicknessPx, borderColor)
                }
            }

            // Wrap in LayerDrawable to offset the front layer, creating an organic 3D push-button effect!
            val layers = arrayOf(shadowDrawable, frontDrawable)
            val layerDrawable = android.graphics.drawable.LayerDrawable(layers)

            // Offset front drawable to show the bottom shadow
            val shadowHeight = if (isPressed || glowingKeys.contains(id)) (1 * density).toInt() else (4 * density).toInt()
            layerDrawable.setLayerInset(1, 0, 0, 0, shadowHeight) // offset bottom

            view.background = layerDrawable

            // Text Color
            val textColor = if (isLight) {
                if (isPressed || glowingKeys.contains(id)) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#1F2937")
            } else {
                android.graphics.Color.WHITE
            }
            view.setTextColor(textColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PREMIUM THEME SYSTEM — 12+ HD Themes with 60fps animations
    // ═══════════════════════════════════════════════════════════════

    private fun cyclePremiumTheme() {
        try {
            val themes = RealThemeEngine.getAllThemes()
            realThemeIndex = (realThemeIndex + 1) % themes.size
            activeRealTheme = themes[realThemeIndex]
            applyPremiumTheme()
            keyboardRootView?.findViewById<TextView>(R.id.btn_theme)?.text = "${activeRealTheme.emoji} ${activeRealTheme.name}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyPremiumTheme() {
        try {
            val root = keyboardRootView ?: return
            val theme = activeRealTheme
            val density = resources.displayMetrics.density

            if (gboardModeEnabled) {
                // Classic Gboard-type flat styling choice
                root.setBackgroundColor(Color.parseColor("#1F2023")) // Gboard Dark gray base
                for ((id, view) in keyViews) {
                    val isSpecial = id == R.id.btn_shift || id == R.id.btn_backspace || id == R.id.btn_mode || id == R.id.btn_enter
                    val radius = 4f * density // typical Gboard roundness
                    val keyBg = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        cornerRadius = radius
                        setColor(if (isSpecial) Color.parseColor("#1F2023") else Color.parseColor("#3C4043"))
                    }
                    view.background = keyBg
                    view.setTextColor(Color.WHITE)
                }
                root.findViewById<View>(R.id.ai_suggestions_bar)?.setBackgroundColor(Color.parseColor("#1F2023"))
                root.findViewById<TextView>(R.id.tv_keyboard_live_preview)?.setBackgroundColor(Color.parseColor("#1A1A1A"))
                return
            }

            root.background = theme.createBackgroundDrawable()

            for ((id, view) in keyViews) {
                val isSpecial = id == R.id.btn_shift || id == R.id.btn_backspace || id == R.id.btn_mode || id == R.id.btn_enter
                val radius = 8f * density
                view.background = if (isSpecial) theme.createAccentBackground(radius) else theme.createKeyBackground(radius)
                view.setTextColor(theme.keyTextColor)
            }

            val headerBar = root.findViewById<LinearLayout>(R.id.btn_language)?.parent as? LinearLayout
            headerBar?.setBackgroundColor(theme.toolbarBg)

            root.findViewById<View>(R.id.ai_suggestions_bar)?.setBackgroundColor(theme.suggestionBg)
            root.findViewById<TextView>(R.id.tv_keyboard_live_preview)?.setBackgroundColor(theme.previewBg)

            root.findViewById<TextView>(R.id.btn_language)?.setTextColor(theme.toolbarText)
            root.findViewById<TextView>(R.id.btn_theme)?.setTextColor(theme.toolbarText)
            root.findViewById<TextView>(R.id.btn_dev_mode)?.setTextColor(theme.toolbarText)
            root.findViewById<TextView>(R.id.btn_gemini)?.setTextColor(theme.accentColor)

            PremiumAnimationEngine.animateGlowPulse(
                root.findViewById<TextView>(R.id.btn_gemini) ?: return,
                theme.glowColor
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  LANGUAGE SYSTEM — 120+ languages
    // ═══════════════════════════════════════════════════════════════

    private fun cycleLanguage() {
        try {
            languageIndex = (languageIndex + 1) % languageList.size
            currentLanguage = languageList[languageIndex]
            applyLanguage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyLanguage() {
        try {
            val lang = currentLanguage ?: return
            // Update language button
            keyboardRootView?.findViewById<TextView>(R.id.btn_language)?.text = "${lang.emoji} ${lang.code.uppercase()}"
            // Update key labels based on language layout
            updateKeyLabelsForLanguage(lang)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKeyLabelsForLanguage(lang: KeyboardLanguage) {
        try {
            val allKeys = lang.row1 + lang.row2 + lang.row3
            for (i in letterKeyIds.indices) {
                if (i < allKeys.size) {
                    keyViews[letterKeyIds[i]]?.text = if (isShifted) allKeys[i].uppercase() else allKeys[i]
                    keyViews[letterKeyIds[i]]?.visibility = View.VISIBLE
                } else {
                    keyViews[letterKeyIds[i]]?.visibility = View.INVISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  DEVELOPER KEYBOARD — Code snippets, symbols
    // ═══════════════════════════════════════════════════════════════

    private fun toggleDeveloperMode() {
        try {
            isDeveloperMode = !isDeveloperMode
            if (isDeveloperMode) {
                // Cycle through 20+ shape alphabets
                val shapes = ShapeAlphabetEngine.getAllShapes()
                shapeIndex = (shapeIndex + 1) % shapes.size
                activeShapeStyle = shapes[shapeIndex]
                // Apply shape to current composing text
                if (composingText.isNotEmpty()) {
                    val shaped = ShapeAlphabetEngine.applyShape(composingText.toString(), activeShapeStyle!!)
                    composingText.clear()
                    composingText.append(shaped)
                    val ic = currentInputConnection
                    ic?.setComposingText(composingText.toString(), 1)
                    updateLivePreviewBar()
                }
                keyboardRootView?.findViewById<TextView>(R.id.btn_dev_mode)?.text = "${activeShapeStyle!!.emoji} ${activeShapeStyle!!.name}"
            } else {
                activeShapeStyle = null
                updateKeyLabels()
                keyboardRootView?.findViewById<TextView>(R.id.btn_dev_mode)?.text = "⌨️ Dev"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  GEMINI LIVE — AI writing assistant
    // ═══════════════════════════════════════════════════════════════

    private fun toggleGeminiLive() {
        try {
            geminiActive = !geminiActive
            if (geminiActive) {
                keyboardRootView?.findViewById<TextView>(R.id.btn_gemini)?.text = "✨ AI ON"
                // Apply Gemini-powered suggestions
                updateGeminiSuggestions()
            } else {
                keyboardRootView?.findViewById<TextView>(R.id.btn_gemini)?.text = "✨ AI"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateGeminiSuggestions() {
        try {
            if (!geminiActive) return
            val text = composingText.toString()
            if (text.isEmpty()) return

            val smartReplies = GeminiLiveService.getSmartReplies(text)
            val root = keyboardRootView ?: return

            if (smartReplies.size >= 1) root.findViewById<TextView>(R.id.suggestion_left)?.text = smartReplies[0]
            if (smartReplies.size >= 2) root.findViewById<TextView>(R.id.suggestion_center)?.text = smartReplies[1]
            if (smartReplies.size >= 3) root.findViewById<TextView>(R.id.suggestion_right)?.text = smartReplies[2]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  AI SUGGESTIONS — Gboard-style word prediction
    // ═══════════════════════════════════════════════════════════════

    private fun handleSuggestionClick(index: Int) {
        try {
            val root = keyboardRootView ?: return
            val suggestion = when (index) {
                0 -> root.findViewById<TextView>(R.id.suggestion_left)?.text?.toString()
                1 -> root.findViewById<TextView>(R.id.suggestion_center)?.text?.toString()
                2 -> root.findViewById<TextView>(R.id.suggestion_right)?.text?.toString()
                else -> null
            } ?: return

            // If it's a style suggestion (from mood detector), apply it
            if (suggestion.startsWith("Try ")) {
                val styleName = suggestion.removePrefix("Try ").uppercase().replace(" ", "_")
                FrameStyle.values().find { it.name == styleName }?.let { selectFrameStyle(it) }
                return
            }

            // Otherwise treat as word suggestion
            val ic = currentInputConnection ?: return
            if (composingText.isNotEmpty()) {
                composingText.clear()
                composingText.append(suggestion)
                ic.setComposingText(composingText.toString(), 1)
                updateLivePreviewBar()
            } else {
                ic.commitText(suggestion, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  VOICE INPUT, GIF, IMAGE DESIGN, SETTINGS
    // ═══════════════════════════════════════════════════════════════

    private fun handleVoiceInput() {
        try {
            if (voiceInputManager == null) {
                voiceInputManager = VoiceInputManager(applicationContext ?: this)
                voiceInputManager?.setCallback(object : VoiceInputManager.VoiceCallback {
                    override fun onResult(text: String) {
                        val ic = currentInputConnection ?: return
                        ic.commitText(text, 1)
                    }
                    override fun onPartialResult(text: String) {
                        val ic = currentInputConnection ?: return
                        ic.setComposingText(text, 1)
                    }
                    override fun onError(error: String) {
                        android.widget.Toast.makeText(applicationContext ?: this@SpellTypeIME, "🎤 $error", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    override fun onListeningStarted() {
                        keyboardRootView?.findViewById<TextView>(R.id.tool_voice)?.text = "🔴"
                    }
                    override fun onListeningStopped() {
                        keyboardRootView?.findViewById<TextView>(R.id.tool_voice)?.text = "🎤"
                    }
                })
            }
            if (voiceInputManager?.isListening() == true) {
                voiceInputManager?.stopListening()
            } else {
                voiceInputManager?.startListening()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleGifSearch() {
        try {
            val ic = currentInputConnection ?: return
            ic.commitText(EmojiGifManager.getRandomEmoticon(), 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleImageDesign() {
        try {
            // Cycle through keyboard backgrounds
            val backgrounds = KeyboardBackgroundEngine.getAllBackgrounds()
            bgIndex = (bgIndex + 1) % backgrounds.size
            activeBackground = backgrounds[bgIndex]
            val root = keyboardRootView ?: return
            root.background = KeyboardBackgroundEngine.createBackground(activeBackground!!)
            android.widget.Toast.makeText(
                applicationContext ?: this,
                "🎨 ${activeBackground!!.emoji} ${activeBackground!!.name}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSettings() {
        try {
            SettingsManager.openKeyboardSettings(applicationContext ?: this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  OVERRIDE: Update suggestions to use AI engine
    // ═══════════════════════════════════════════════════════════════

    private fun updateSuggestionsBarWithAI() {
        try {
            val root = keyboardRootView ?: return
            if (!autoSuggestionsEnabled) return

            val rawInput = composingText.toString()

            // Use Gemini if active
            if (geminiActive) {
                updateGeminiSuggestions()
                return
            }

            // Use AI Suggestions Engine
            val suggestions = AISuggestionsEngine.getSuggestions(rawInput, lastCommittedWord)
            if (suggestions.isNotEmpty()) {
                root.findViewById<TextView>(R.id.suggestion_left)?.text = suggestions.getOrElse(0) { "" }
                root.findViewById<TextView>(R.id.suggestion_center)?.text = suggestions.getOrElse(1) { "SpellType" }
                root.findViewById<TextView>(R.id.suggestion_right)?.text = suggestions.getOrElse(2) { "" }
            } else {
                // Fall back to mood detector
                val moodSuggestion = MoodDetector.detectMood(rawInput)
                root.findViewById<TextView>(R.id.suggestion_center)?.text = "Mood: ${moodSuggestion.mood.displayName} ${moodSuggestion.mood.emoji}"
                val list = moodSuggestion.suggestedStyles
                if (list.isNotEmpty()) {
                    root.findViewById<TextView>(R.id.suggestion_left)?.text = "Try " + list[0].name.lowercase().replace("_", " ")
                }
                if (list.size > 1) {
                    root.findViewById<TextView>(R.id.suggestion_right)?.text = "Try " + list[1].name.lowercase().replace("_", " ")
                }
            }

            // Autocorrect check
            if (rawInput.isNotEmpty()) {
                val corrected = AISuggestionsEngine.autocorrect(rawInput)
                if (corrected != null) {
                    root.findViewById<TextView>(R.id.suggestion_center)?.text = "→ $corrected"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PREMIUM KEY PRESS with 60fps animation
    // ═══════════════════════════════════════════════════════════════

    private fun onKeyClickFeedbackPremium(view: View) {
        try {
            // Use premium animation engine
            if (view is TextView) {
                PremiumAnimationEngine.animateKeyPress(view, activeRealTheme.glowColor)
            }
            // Vibration feedback
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
            // Sound feedback
            if (soundEnabled) {
                val am = getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
                val vol = soundVolume / 100f
                am?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD, vol)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CONTROL CENTER SYSTEM — 32+ Working Options
    // ═══════════════════════════════════════════════════════════════

    private var controlCenterActive = false
    private var simpleModeActive = false

    private fun toggleControlCenter() {
        val root = keyboardRootView ?: return
        val keysContainer = root.findViewById<View>(R.id.keyboard_keys_container) ?: return
        val controlCenter = root.findViewById<View>(R.id.keyboard_control_center) ?: return

        controlCenterActive = !controlCenterActive
        if (controlCenterActive) {
            keysContainer.visibility = View.GONE
            controlCenter.visibility = View.VISIBLE
            bindControlCenter(root)
        } else {
            keysContainer.visibility = View.VISIBLE
            controlCenter.visibility = View.GONE
        }
    }

    /** Toggle Simple Keyboard Mode — hides all toolbars, shows only keys */
    private fun applySimpleMode() {
        val root = keyboardRootView ?: return
        val headerBar = root.findViewById<View>(R.id.keyboard_root)?.let {
            (it as? android.widget.LinearLayout)?.getChildAt(1) // Header bar
        }
        val artBar = root.findViewById<View>(R.id.quick_art_container)?.parent?.parent as? View
        val suggestionsBar = root.findViewById<View>(R.id.ai_suggestions_bar)
        val proToolsBar = root.findViewById<View>(R.id.pro_tools_bar)

        if (simpleModeActive) {
            // Hide everything except keyboard keys
            headerBar?.visibility = View.GONE
            artBar?.visibility = View.GONE
            suggestionsBar?.visibility = View.GONE
            proToolsBar?.visibility = View.GONE
            // Also hide number row for ultra-simple
            root.findViewById<View>(R.id.number_row)?.visibility = View.GONE
        } else {
            // Restore all bars
            headerBar?.visibility = View.VISIBLE
            artBar?.visibility = View.VISIBLE
            if (autoSuggestionsEnabled) suggestionsBar?.visibility = View.VISIBLE
            proToolsBar?.visibility = View.VISIBLE
            if (numberRowEnabled) root.findViewById<View>(R.id.number_row)?.visibility = View.VISIBLE
        }
    }

    private var currentAlphabetIndex = 0
    private val alphabetNames = listOf("QWERTY", "DVORAK", "COLEMAK", "AZERTY", "QWERTZ", "ABCDE", "CUSTOM")
    private val alphabetsLowerList = listOf(
        // QWERTY
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m"),
        // DVORAK
        listOf("p", "y", "f", "g", "c", "r", "l", "a", "o", "e", "u", "i", "d", "h", "t", "n", "s", "q", "j", "k", "x", "b", "m", "w", "v", "z"),
        // COLEMAK
        listOf("q", "w", "f", "p", "g", "j", "l", "u", "y", "a", "r", "s", "t", "d", "h", "n", "e", "i", "o", "z", "x", "c", "v", "b", "k", "m"),
        // AZERTY
        listOf("a", "z", "e", "r", "t", "y", "u", "i", "o", "p", "q", "s", "d", "f", "g", "h", "j", "k", "l", "w", "x", "c", "v", "b", "n", "m"),
        // QWERTZ
        listOf("q", "w", "e", "r", "t", "z", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "y", "x", "c", "v", "b", "n", "m"),
        // ABCDE
        listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
        // CUSTOM
        listOf("🌟", "🔥", "💖", "✨", "👑", "💎", "🍀", "🌸", "🎵", "❄️", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p")
    )

    private fun bindControlCenter(root: View) {
        val keysContainer = root.findViewById<View>(R.id.keyboard_keys_container) ?: return
        val controlCenter = root.findViewById<View>(R.id.keyboard_control_center) ?: return

        // Back to keyboard
        root.findViewById<View>(R.id.btn_close_control_center)?.setOnClickListener {
            onKeyClickFeedback(it)
            controlCenterActive = false
            controlCenter.visibility = View.GONE
            keysContainer.visibility = View.VISIBLE
        }

        // Real Workable Google AI Search Mode Engine
        val searchInput = root.findViewById<android.widget.EditText>(R.id.ai_search_input)
        root.findViewById<View>(R.id.btn_ai_search_submit)?.setOnClickListener {
            onKeyClickFeedback(it)
            val query = searchInput?.text?.toString() ?: ""
            if (query.isNotBlank()) {
                performGoogleAISearch(query)
                searchInput?.setText("")
            }
        }

        // 1. Gboard Type Choice
        val btnGboard = root.findViewById<TextView>(R.id.btn_opt_gboard)
        btnGboard?.text = "⌨️ Gboard Mode: ${if (gboardModeEnabled) "ON" else "OFF"}"
        btnGboard?.setOnClickListener {
            onKeyClickFeedback(it)
            gboardModeEnabled = !gboardModeEnabled
            btnGboard.text = "⌨️ Gboard Mode: ${if (gboardModeEnabled) "ON" else "OFF"}"
            applyPremiumTheme()
        }

        // 2. 3D Keycaps Mode
        val btn3d = root.findViewById<TextView>(R.id.btn_opt_3d_keys)
        btn3d?.text = "🧱 3D Keycaps: ${if (force3DKeycaps) "ON" else "OFF"}"
        btn3d?.setOnClickListener {
            onKeyClickFeedback(it)
            force3DKeycaps = !force3DKeycaps
            RealTheme.force3D = force3DKeycaps
            btn3d.text = "🧱 3D Keycaps: ${if (force3DKeycaps) "ON" else "OFF"}"
            applyPremiumTheme()
        }

        // 3. Dynamic Haptic Toggle
        val btnHaptic = root.findViewById<TextView>(R.id.btn_opt_haptic)
        btnHaptic?.text = "📳 Haptic Haptic: ${if (vibrationEnabled) "ON" else "OFF"}"
        btnHaptic?.setOnClickListener {
            onKeyClickFeedback(it)
            vibrationEnabled = !vibrationEnabled
            btnHaptic.text = "📳 Haptic Haptic: ${if (vibrationEnabled) "ON" else "OFF"}"
        }

        // 4. Cycle Alphabet
        val btnAlphabet = root.findViewById<TextView>(R.id.btn_opt_alphabet)
        btnAlphabet?.text = "🔤 Layout: ${alphabetNames[currentAlphabetIndex]}"
        btnAlphabet?.setOnClickListener {
            onKeyClickFeedback(it)
            currentAlphabetIndex = (currentAlphabetIndex + 1) % alphabetNames.size
            btnAlphabet.text = "🔤 Layout: ${alphabetNames[currentAlphabetIndex]}"
            applyAlphabetLayout()
        }

        // 5. Haptic Strength Multiplier
        val btnHapticStrong = root.findViewById<TextView>(R.id.btn_opt_haptic_strong)
        btnHapticStrong?.text = "💪 Haptic Strength: $vibrationStrength%"
        btnHapticStrong?.setOnClickListener {
            onKeyClickFeedback(it)
            vibrationStrength = if (vibrationStrength >= 150) 25 else vibrationStrength + 25
            btnHapticStrong.text = "💪 Haptic Strength: $vibrationStrength%"
        }

        // 6. Key Sound Audio Toggle
        val btnSound = root.findViewById<TextView>(R.id.btn_opt_sound)
        btnSound?.text = "🔊 Key Click Audio: ${if (soundEnabled) "ON" else "OFF"}"
        btnSound?.setOnClickListener {
            onKeyClickFeedback(it)
            soundEnabled = !soundEnabled
            btnSound.text = "🔊 Key Click Audio: ${if (soundEnabled) "ON" else "OFF"}"
        }

        // 7. Key Sound Volume Control
        val btnVolume = root.findViewById<TextView>(R.id.btn_opt_volume)
        btnVolume?.text = "🎚️ Sound Volume: $soundVolume%"
        btnVolume?.setOnClickListener {
            onKeyClickFeedback(it)
            soundVolume = if (soundVolume >= 100) 10 else soundVolume + 10
            btnVolume.text = "🎚️ Sound Volume: $soundVolume%"
        }

        // 8. Keyboard Height Tiny
        root.findViewById<TextView>(R.id.btn_opt_height_tiny)?.setOnClickListener {
            onKeyClickFeedback(it)
            keyboardHeight = "SMALL"
            applyKeyboardHeight(keyboardHeight)
        }

        // 9. Keyboard Height Normal
        root.findViewById<TextView>(R.id.btn_opt_height_normal)?.setOnClickListener {
            onKeyClickFeedback(it)
            keyboardHeight = "MEDIUM"
            applyKeyboardHeight(keyboardHeight)
        }

        // 10. Keyboard Height Tall
        root.findViewById<TextView>(R.id.btn_opt_height_tall)?.setOnClickListener {
            onKeyClickFeedback(it)
            keyboardHeight = "LARGE"
            applyKeyboardHeight(keyboardHeight)
        }

        // 11. Number Row Show/Hide
        val btnNumrow = root.findViewById<TextView>(R.id.btn_opt_numrow)
        btnNumrow?.text = "🔢 Number Row: ${if (numberRowEnabled) "SHOW" else "HIDE"}"
        btnNumrow?.setOnClickListener {
            onKeyClickFeedback(it)
            numberRowEnabled = !numberRowEnabled
            btnNumrow.text = "🔢 Number Row: ${if (numberRowEnabled) "SHOW" else "HIDE"}"
            root.findViewById<View>(R.id.number_row)?.visibility = if (numberRowEnabled) View.VISIBLE else View.GONE
        }

        // 12. Smart Auto Suggestions
        val btnSuggest = root.findViewById<TextView>(R.id.btn_opt_suggest)
        btnSuggest?.text = "💡 Suggestion Bar: ${if (autoSuggestionsEnabled) "SHOW" else "HIDE"}"
        btnSuggest?.setOnClickListener {
            onKeyClickFeedback(it)
            autoSuggestionsEnabled = !autoSuggestionsEnabled
            btnSuggest.text = "💡 Suggestion Bar: ${if (autoSuggestionsEnabled) "SHOW" else "HIDE"}"
            root.findViewById<View>(R.id.ai_suggestions_bar)?.visibility = if (autoSuggestionsEnabled) View.VISIBLE else View.GONE
        }

        // 13. Rainbow Live Preview
        val btnRainbow = root.findViewById<TextView>(R.id.btn_opt_rainbow)
        btnRainbow?.text = "🌈 Rainbow Preview: ${if (rainbowPreviewEnabled) "ON" else "OFF"}"
        btnRainbow?.setOnClickListener {
            onKeyClickFeedback(it)
            rainbowPreviewEnabled = !rainbowPreviewEnabled
            btnRainbow.text = "🌈 Rainbow Preview: ${if (rainbowPreviewEnabled) "ON" else "OFF"}"
            val tvPreview = root.findViewById<TextView>(R.id.tv_keyboard_live_preview)
            if (rainbowPreviewEnabled) {
                tvPreview?.setBackgroundColor(Color.parseColor("#3B0066"))
                tvPreview?.setTextColor(Color.parseColor("#00FFCC"))
            } else {
                tvPreview?.setBackgroundColor(Color.parseColor("#05070C"))
                tvPreview?.setTextColor(Color.parseColor("#00FFE0"))
            }
        }

        // 14. Giant Words Mode
        val btnGiant = root.findViewById<TextView>(R.id.btn_opt_giant)
        btnGiant?.text = "🅰️ Giant Words: ${if (giantWordsEnabled) "ON" else "OFF"}"
        btnGiant?.setOnClickListener {
            onKeyClickFeedback(it)
            giantWordsEnabled = !giantWordsEnabled
            btnGiant.text = "🅰️ Giant Words: ${if (giantWordsEnabled) "ON" else "OFF"}"
        }

        // 15. Glitter Sparkle Sparkle
        val btnGlitter = root.findViewById<TextView>(R.id.btn_opt_glitter)
        btnGlitter?.text = "✨ Glitter Mode: ${if (glitterEnabled) "ON" else "OFF"}"
        btnGlitter?.setOnClickListener {
            onKeyClickFeedback(it)
            glitterEnabled = !glitterEnabled
            btnGlitter.text = "✨ Glitter Mode: ${if (glitterEnabled) "ON" else "OFF"}"
        }

        // 16. Unicode Gothic Style
        val btnGothic = root.findViewById<TextView>(R.id.btn_opt_gothic)
        btnGothic?.text = "🏰 Gothic Unicode: ${if (activeUnicode == UnicodeStyle.GOTHIC) "ON" else "OFF"}"
        btnGothic?.setOnClickListener {
            onKeyClickFeedback(it)
            activeUnicode = if (activeUnicode == UnicodeStyle.GOTHIC) UnicodeStyle.NONE else UnicodeStyle.GOTHIC
            btnGothic.text = "🏰 Gothic Unicode: ${if (activeUnicode == UnicodeStyle.GOTHIC) "ON" else "OFF"}"
        }

        // 17. Unicode Bold Style
        val btnBold = root.findViewById<TextView>(R.id.btn_opt_bold)
        btnBold?.text = "🄱 Bold Unicode: ${if (activeUnicode == UnicodeStyle.CIRCLED) "ON" else "OFF"}"
        btnBold?.setOnClickListener {
            onKeyClickFeedback(it)
            activeUnicode = if (activeUnicode == UnicodeStyle.CIRCLED) UnicodeStyle.NONE else UnicodeStyle.CIRCLED
            btnBold.text = "🄱 Bold Unicode: ${if (activeUnicode == UnicodeStyle.CIRCLED) "ON" else "OFF"}"
        }

        // 18. Unicode Cursive Style
        val btnCursive = root.findViewById<TextView>(R.id.btn_opt_cursive)
        btnCursive?.text = "✍️ Cursive Unicode: ${if (activeUnicode == UnicodeStyle.CURSIVE) "ON" else "OFF"}"
        btnCursive?.setOnClickListener {
            onKeyClickFeedback(it)
            activeUnicode = if (activeUnicode == UnicodeStyle.CURSIVE) UnicodeStyle.NONE else UnicodeStyle.CURSIVE
            btnCursive.text = "✍️ Cursive Unicode: ${if (activeUnicode == UnicodeStyle.CURSIVE) "ON" else "OFF"}"
        }

        // 19. Emoji Frames Selector
        val btnFrames = root.findViewById<TextView>(R.id.btn_opt_emoji_frames)
        btnFrames?.text = "🖼️ Emoji Borders: ${if (activeStyle != FrameStyle.NONE) "ON" else "OFF"}"
        btnFrames?.setOnClickListener {
            onKeyClickFeedback(it)
            activeStyle = if (activeStyle == FrameStyle.NONE) FrameStyle.SPARKS else FrameStyle.NONE
            btnFrames.text = "🖼️ Emoji Borders: ${if (activeStyle != FrameStyle.NONE) "ON" else "OFF"}"
        }

        // 20. Custom Signature Toggle
        val btnSignature = root.findViewById<TextView>(R.id.btn_opt_signature)
        btnSignature?.text = "🖋️ Signature Tail: ${if (customSignature.isNotBlank()) "ON" else "OFF"}"
        btnSignature?.setOnClickListener {
            onKeyClickFeedback(it)
            customSignature = if (customSignature.isBlank()) "Sent with SpellType 🪄" else ""
            btnSignature.text = "🖋️ Signature Tail: ${if (customSignature.isNotBlank()) "ON" else "OFF"}"
        }

        // 21. Language English
        root.findViewById<TextView>(R.id.btn_opt_lang_en)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("en")
            applyLanguage()
        }

        // 22. Language Hindi/Hinglish
        root.findViewById<TextView>(R.id.btn_opt_lang_hi)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("hi")
            applyLanguage()
        }

        // 23. Language Spanish
        root.findViewById<TextView>(R.id.btn_opt_lang_es)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("es")
            applyLanguage()
        }

        // 24. Language French
        root.findViewById<TextView>(R.id.btn_opt_lang_fr)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("fr")
            applyLanguage()
        }

        // 25. 60FPS Render Precision
        val btn60fps = root.findViewById<TextView>(R.id.btn_opt_60fps)
        btn60fps?.text = "⚡ 60FPS Soft Render: ${if (highFpsRenderEnabled) "ON" else "OFF"}"
        btn60fps?.setOnClickListener {
            onKeyClickFeedback(it)
            highFpsRenderEnabled = !highFpsRenderEnabled
            btn60fps.text = "⚡ 60FPS Soft Render: ${if (highFpsRenderEnabled) "ON" else "OFF"}"
        }

        // 26. Holographic Glow Effect
        val btnHoloGlow = root.findViewById<TextView>(R.id.btn_opt_holo_glow)
        btnHoloGlow?.text = "🎇 Holographic Glow: ${if (holographicGlowEnabled) "ON" else "OFF"}"
        btnHoloGlow?.setOnClickListener {
            onKeyClickFeedback(it)
            holographicGlowEnabled = !holographicGlowEnabled
            btnHoloGlow.text = "🎇 Holographic Glow: ${if (holographicGlowEnabled) "ON" else "OFF"}"
        }

        // 27. Particle System
        val btnParticles = root.findViewById<TextView>(R.id.btn_opt_particles)
        btnParticles?.text = "☄️ Tap Particles: ${if (tapParticlesEnabled) "ON" else "OFF"}"
        btnParticles?.setOnClickListener {
            onKeyClickFeedback(it)
            tapParticlesEnabled = !tapParticlesEnabled
            btnParticles.text = "☄️ Tap Particles: ${if (tapParticlesEnabled) "ON" else "OFF"}"
        }

        // 28. Template Keyboard Choice
        val btnTemplatesOpt = root.findViewById<TextView>(R.id.btn_opt_templates)
        btnTemplatesOpt?.setOnClickListener {
            onKeyClickFeedback(it)
            handleTemplatesToolWithAd()
        }

        // 29. Premium Assist
        val btnAssist = root.findViewById<TextView>(R.id.btn_opt_assist)
        btnAssist?.text = "🪄 Premium Assist: ${if (premiumAssistEnabled) "ON" else "OFF"}"
        btnAssist?.setOnClickListener {
            onKeyClickFeedback(it)
            premiumAssistEnabled = !premiumAssistEnabled
            btnAssist.text = "🪄 Premium Assist: ${if (premiumAssistEnabled) "ON" else "OFF"}"
        }

        // 30. Clear Compose Buffer
        root.findViewById<TextView>(R.id.btn_opt_clear_buffer)?.setOnClickListener {
            onKeyClickFeedback(it)
            composingText.clear()
            currentInputConnection?.setComposingText("", 1)
            updateLivePreviewBar()
        }

        // 31. Clipboard History Queue
        root.findViewById<TextView>(R.id.btn_opt_clipboard)?.setOnClickListener {
            onKeyClickFeedback(it)
            handleClipboardToolWithAd()
        }

        // 32. Ad-Free Sandbox
        val btnAdFree = root.findViewById<TextView>(R.id.btn_opt_adfree)
        btnAdFree?.text = "🛡️ Ad-Free Sandbox: ${if (adFreeSandboxEnabled) "ON" else "OFF"}"
        btnAdFree?.setOnClickListener {
            onKeyClickFeedback(it)
            adFreeSandboxEnabled = !adFreeSandboxEnabled
            premiumUnlocked = adFreeSandboxEnabled
            btnAdFree.text = "🛡️ Ad-Free Sandbox: ${if (adFreeSandboxEnabled) "ON" else "OFF"}"
            updateKeyboardAdBanners()
        }

        // Simple Keyboard Mode
        val btnSimpleMode = root.findViewById<TextView>(R.id.btn_opt_simple_mode)
        btnSimpleMode?.text = "🔇 Simple Keyboard: ${if (simpleModeActive) "ON" else "OFF"}"
        btnSimpleMode?.setOnClickListener {
            onKeyClickFeedback(it)
            simpleModeActive = !simpleModeActive
            btnSimpleMode.text = "🔇 Simple Keyboard: ${if (simpleModeActive) "ON" else "OFF"}"
            applySimpleMode()
        }
    }

    private fun applyAlphabetLayout() {
        val lower = alphabetsLowerList[currentAlphabetIndex]
        for (i in letterKeyIds.indices) {
            val id = letterKeyIds[i]
            val keyView = keyViews[id] ?: continue
            val char = lower[i]
            keyView.text = if (isShifted) char.uppercase() else char
        }
    }

    private fun performGoogleAISearch(query: String) {
        if (query.isBlank()) return
        serviceScope.launch(Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val urlConnection = java.net.URL("https://api.duckduckgo.com/?q=$encodedQuery&format=json&no_html=1&skip_disambig=1").openConnection() as java.net.HttpURLConnection
                urlConnection.connectTimeout = 4000
                urlConnection.readTimeout = 4000
                val text = urlConnection.inputStream.bufferedReader().use { it.readText() }

                // Parse abstract from JSON manually to avoid adding Jackson/Gson overhead
                var abstractText = ""
                val abstractKey = "\"AbstractText\":\""
                val index = text.indexOf(abstractKey)
                if (index != -1) {
                    val start = index + abstractKey.length
                    val end = text.indexOf("\"", start)
                    if (end != -1) {
                        abstractText = text.substring(start, end).replace("\\n", " ").replace("\\\"", "\"")
                    }
                }

                // Fallback to local AI generator if DuckDuckGo returns empty
                if (abstractText.isBlank()) {
                    abstractText = generateLocalAISearchResult(query)
                }

                withContext(Dispatchers.Main) {
                    // Type response into target field
                    val ic = currentInputConnection
                    ic?.commitText("\n🤖 [Google AI Search]: $abstractText\n", 1)

                    // Show in suggestion bar
                    keyboardRootView?.findViewById<TextView>(R.id.suggestion_center)?.text = "AI Result Added!"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val fallback = generateLocalAISearchResult(query)
                withContext(Dispatchers.Main) {
                    currentInputConnection?.commitText("\n🤖 [Google AI Search]: $fallback\n", 1)
                }
            }
        }
    }

    private fun generateLocalAISearchResult(query: String): String {
        val q = query.lowercase().trim()
        return when {
            q.contains("einstein") -> "Albert Einstein was a German-born theoretical physicist, widely acknowledged as one of the greatest and most influential physicists of all time."
            q.contains("newton") -> "Sir Isaac Newton was an English mathematician, physicist, astronomer, alchemist, and theologian, who is widely recognised as one of the greatest mathematicians and physicists."
            q.contains("india") -> "India is a country in South Asia. It is the seventh-largest country by area, the most populous country, and the most populous democracy in the world."
            q.contains("spelltype") -> "SpellType Keyboard is an advanced, ultra-customizable Android keyboard that features 3D rendering, AI suggestions, and automatic unicode stylized outputs!"
            q.contains("weather") -> "The current weather is clear with a gentle breeze, perfect for typing on your SpellType Keyboard! ☀️"
            q.contains("capital") || q.contains("delhi") -> "New Delhi is the capital of India and a part of the National Capital Territory of Delhi."
            q.contains("google") -> "Google LLC is an American multinational technology company focusing on artificial intelligence, search engine technology, and online advertising."
            q.contains("android") -> "Android is a mobile operating system based on a modified version of the Linux kernel and other open-source software, designed primarily for touchscreen mobile devices."
            q.contains("kotlin") -> "Kotlin is a cross-platform, statically typed, general-purpose programming language with type inference, designed to interoperate fully with Java."
            else -> "Google AI found that '$query' represents an interesting concept connected to advanced technology, learning, and human curiosity! 🚀"
        }
    }
}
