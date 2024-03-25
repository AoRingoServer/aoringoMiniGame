package com.github.AoRingoServer

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

interface GUI {
    val guiName: String
    fun make(player: Player): Inventory
    fun clickProcess(item: ItemStack, player: Player, isShift: Boolean)
}
