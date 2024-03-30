package com.github.AoRingoServer.CookGame

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface MultiplePageGUI {
    fun makeMenuGUI(pageNumber: Int, player: Player): Inventory
}
