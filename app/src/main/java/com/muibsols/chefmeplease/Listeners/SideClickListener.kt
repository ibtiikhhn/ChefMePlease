package com.muibsols.chefmeplease.Listeners

import android.view.WindowInsets
import com.muibsols.chefmeplease.Models.Side

interface SideClickListener {
    fun onAddSide(side: Side)
    fun onRemoveSide(side: Side)
}