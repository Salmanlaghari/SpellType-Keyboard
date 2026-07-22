package com.spelltype.keyboard.presentation.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spelltype.keyboard.databinding.ItemSavedArtBinding
import com.spelltype.keyboard.domain.model.SavedArt

class SavedArtAdapter(
    private val onDeleteClick: (SavedArt) -> Unit
) : ListAdapter<SavedArt, SavedArtAdapter.SavedArtViewHolder>(SavedArtDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedArtViewHolder {
        val binding = ItemSavedArtBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedArtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedArtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedArtViewHolder(
        private val binding: ItemSavedArtBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(art: SavedArt) {
            binding.tvStyleName.text = "STYLE: ${art.styleName}"
            binding.tvOriginalText.text = "Original: ${art.originalText}"
            binding.tvStyledText.text = art.styledText
            binding.btnDelete.setOnClickListener {
                onDeleteClick(art)
            }
        }
    }

    class SavedArtDiffCallback : DiffUtil.ItemCallback<SavedArt>() {
        override fun areItemsTheSame(oldItem: SavedArt, newItem: SavedArt): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedArt, newItem: SavedArt): Boolean {
            return oldItem == newItem
        }
    }
}
