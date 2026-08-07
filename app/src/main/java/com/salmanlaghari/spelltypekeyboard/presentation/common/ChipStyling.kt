package com.salmanlaghari.spelltypekeyboard.presentation.common

import android.view.View
import com.salmanlaghari.spelltypekeyboard.R

/** Applies the shared active/inactive chip background used across settings and the keyboard bars. */
fun View.setChipSelected(selected: Boolean) {
    setBackgroundResource(
        if (selected) R.drawable.chip_active_background else R.drawable.chip_inactive_background
    )
}
