package com.spelltype.keyboard.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.spelltype.keyboard.R
import com.spelltype.keyboard.databinding.ActivitySettingsBinding
import com.spelltype.keyboard.data.db.SpellTypeDatabase
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.data.repository.KeyboardRepositoryImpl
import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.ShapeEngine
import com.spelltype.keyboard.domain.UnicodeStylingEngine
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private val adapter = SavedArtAdapter(
        onDeleteClick = { art -> viewModel.deleteArt(art) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = SpellTypeDatabase.getDatabase(applicationContext)
        val preferences = KeyboardPreferences(applicationContext)
        val repository = KeyboardRepositoryImpl(database.savedArtDao(), preferences)
        viewModel = ViewModelProvider(this, SettingsViewModelFactory(repository))[SettingsViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupListeners() {
        // Guide Button 1: Enable settings
        binding.btnEnableSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        // Guide Button 2: Select active IME
        binding.btnSelectIme.setOnClickListener {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.showInputMethodPicker()
        }

        // Frame selection chips
        binding.settChipNone.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.NONE) }
        binding.settChipBox.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX) }
        binding.settChipBoxDouble.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX_DOUBLE) }
        binding.settChipBoxRounded.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX_ROUNDED) }
        binding.settChipStar.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.STAR) }
        binding.settChipDiamond.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.DIAMOND) }
        binding.settChipFire.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.FIRE) }
        binding.settChipHearts.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.HEARTS) }
        binding.settChipSparks.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.SPARKS) }
        binding.settChipParty.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.PARTY) }

        // Shape selection chips
        binding.settShapeNone.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.NONE) }
        binding.settShapePyramid.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.PYRAMID) }
        binding.settShapeHeart.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.HEART) }
        binding.settShapeDiamond.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.DIAMOND) }
        binding.settShapeZigzag.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.ZIGZAG) }
        binding.settShapeWave.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.WAVE) }

        // Unicode styling chips
        binding.settUnicodeNone.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.NONE) }
        binding.settUnicodeBold.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.BOLD) }
        binding.settUnicodeItalic.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.ITALIC) }
        binding.settUnicodeGothic.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.GOTHIC) }
        binding.settUnicodeCursive.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.CURSIVE) }
        binding.settUnicodeCircled.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.CIRCLED) }
        binding.settUnicodeSquared.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.SQUARED) }
        binding.settUnicodeSquaredSolid.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.SQUARED_SOLID) }
        binding.settUnicodeBubble.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.BUBBLE) }

        // Glitter Switch listener
        binding.switchGlitter.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setGlitterEnabled(isChecked)
        }

        // Custom signature input listener
        binding.etCustomSignature.addTextChangedListener { text ->
            viewModel.setCustomSignature(text?.toString() ?: "")
        }

        // Live preview input listener
        binding.etPreviewInput.addTextChangedListener {
            updateLivePreview()
        }

        // Clear all history listener
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllArt()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect and highlight selected frame style
                launch {
                    viewModel.selectedFrameStyle.collect { style ->
                        updateFrameHighlighting(style)
                        updateLivePreview()
                    }
                }

                // Collect and highlight selected shape layout
                launch {
                    viewModel.selectedShapeLayout.collect { shape ->
                        updateShapeHighlighting(shape)
                        updateLivePreview()
                    }
                }

                // Collect and highlight selected unicode style
                launch {
                    viewModel.selectedUnicodeStyle.collect { unicode ->
                        updateUnicodeHighlighting(unicode)
                        updateLivePreview()
                    }
                }

                // Collect and set glitter toggle state
                launch {
                    viewModel.glitterEnabled.collect { enabled ->
                        if (binding.switchGlitter.isChecked != enabled) {
                            binding.switchGlitter.isChecked = enabled
                        }
                        updateLivePreview()
                    }
                }

                // Collect custom signature text
                launch {
                    viewModel.customSignature.collect { signature ->
                        if (binding.etCustomSignature.text?.toString() != signature) {
                            binding.etCustomSignature.setText(signature)
                        }
                        updateLivePreview()
                    }
                }

                // Collect saved history list
                launch {
                    viewModel.savedArtList.collect { list ->
                        if (list.isEmpty()) {
                            binding.rvHistory.visibility = View.GONE
                            binding.tvEmptyHistory.visibility = View.VISIBLE
                        } else {
                            binding.rvHistory.visibility = View.VISIBLE
                            binding.tvEmptyHistory.visibility = View.GONE
                            adapter.submitList(list)
                        }
                    }
                }
            }
        }
    }

    private fun updateFrameHighlighting(active: FrameStyle) {
        binding.settChipNone.setBackgroundResource(if (active == FrameStyle.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBox.setBackgroundResource(if (active == FrameStyle.BOX) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBoxDouble.setBackgroundResource(if (active == FrameStyle.BOX_DOUBLE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBoxRounded.setBackgroundResource(if (active == FrameStyle.BOX_ROUNDED) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipStar.setBackgroundResource(if (active == FrameStyle.STAR) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipDiamond.setBackgroundResource(if (active == FrameStyle.DIAMOND) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipFire.setBackgroundResource(if (active == FrameStyle.FIRE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipHearts.setBackgroundResource(if (active == FrameStyle.HEARTS) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipSparks.setBackgroundResource(if (active == FrameStyle.SPARKS) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipParty.setBackgroundResource(if (active == FrameStyle.PARTY) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateShapeHighlighting(active: ShapeLayout) {
        binding.settShapeNone.setBackgroundResource(if (active == ShapeLayout.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapePyramid.setBackgroundResource(if (active == ShapeLayout.PYRAMID) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeHeart.setBackgroundResource(if (active == ShapeLayout.HEART) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeDiamond.setBackgroundResource(if (active == ShapeLayout.DIAMOND) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeZigzag.setBackgroundResource(if (active == ShapeLayout.ZIGZAG) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeWave.setBackgroundResource(if (active == ShapeLayout.WAVE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateUnicodeHighlighting(active: UnicodeStyle) {
        binding.settUnicodeNone.setBackgroundResource(if (active == UnicodeStyle.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeBold.setBackgroundResource(if (active == UnicodeStyle.BOLD) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeItalic.setBackgroundResource(if (active == UnicodeStyle.ITALIC) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeGothic.setBackgroundResource(if (active == UnicodeStyle.GOTHIC) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeCursive.setBackgroundResource(if (active == UnicodeStyle.CURSIVE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeCircled.setBackgroundResource(if (active == UnicodeStyle.CIRCLED) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeSquared.setBackgroundResource(if (active == UnicodeStyle.SQUARED) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeSquaredSolid.setBackgroundResource(if (active == UnicodeStyle.SQUARED_SOLID) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeBubble.setBackgroundResource(if (active == UnicodeStyle.BUBBLE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateLivePreview() {
        val text = binding.etPreviewInput.text?.toString() ?: ""
        if (text.isEmpty()) {
            binding.tvRealtimePreview.text = getString(R.string.type_preview_hint)
            return
        }

        val unicode = viewModel.selectedUnicodeStyle.value
        val shape = viewModel.selectedShapeLayout.value
        val frame = viewModel.selectedFrameStyle.value
        val glitter = viewModel.glitterEnabled.value
        val signature = viewModel.customSignature.value

        var processed = UnicodeStylingEngine.applyStyle(text, unicode)

        if (glitter) {
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

        processed = ShapeEngine.applyShape(processed, shape)
        processed = ArtEngine.applyFrame(processed, frame)
        if (signature.isNotEmpty()) {
            processed = "$processed\n$signature"
        }

        binding.tvRealtimePreview.text = processed
    }
}
