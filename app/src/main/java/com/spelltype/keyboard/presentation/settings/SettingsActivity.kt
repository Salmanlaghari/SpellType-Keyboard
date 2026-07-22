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
import com.spelltype.keyboard.domain.model.FrameStyle
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

        // Chip selection listeners
        binding.settChipNone.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.NONE) }
        binding.settChipBox.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX) }
        binding.settChipStar.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.STAR) }
        binding.settChipBracket.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BRACKET) }
        binding.settChipDiamond.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.DIAMOND) }

        // Live preview input listener
        binding.etPreviewInput.addTextChangedListener { text ->
            updateLivePreview(text?.toString() ?: "", viewModel.selectedFrameStyle.value)
        }

        // Clear all history listener
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllArt()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect selected frame style
                launch {
                    viewModel.selectedFrameStyle.collect { style ->
                        updateChipHighlighting(style)
                        updateLivePreview(binding.etPreviewInput.text?.toString() ?: "", style)
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

    private fun updateChipHighlighting(activeStyle: FrameStyle) {
        binding.settChipNone.setBackgroundResource(if (activeStyle == FrameStyle.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBox.setBackgroundResource(if (activeStyle == FrameStyle.BOX) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipStar.setBackgroundResource(if (activeStyle == FrameStyle.STAR) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBracket.setBackgroundResource(if (activeStyle == FrameStyle.BRACKET) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipDiamond.setBackgroundResource(if (activeStyle == FrameStyle.DIAMOND) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateLivePreview(text: String, style: FrameStyle) {
        if (text.isEmpty()) {
            binding.tvRealtimePreview.text = getString(R.string.type_preview_hint)
        } else {
            binding.tvRealtimePreview.text = ArtEngine.applyFrame(text, style)
        }
    }
}
