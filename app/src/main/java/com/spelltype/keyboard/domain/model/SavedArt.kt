package com.spelltype.keyboard.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_art")
data class SavedArt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val styledText: String,
    val styleName: String,
    val timestamp: Long = System.currentTimeMillis()
)
