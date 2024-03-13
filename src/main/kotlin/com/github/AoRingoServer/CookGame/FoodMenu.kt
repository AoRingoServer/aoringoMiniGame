package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.GUIs
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodMenu(private val plugin: Plugin) : GUIs {
    override val guiName: String = "${ChatColor.DARK_BLUE}メニュー"
    override fun make(player: Player): Inventory {
        val foodManager = FoodManager(plugin)
        val gui = Bukkit.createInventory(null, 45, guiName)
        val foodInfoList = foodManager.foodInfoList()
        for (foodID in foodInfoList) {
            val foodInfo = foodManager.makeFoodInfo(foodID)
            val food = foodManager.makeFoodItem(foodInfo)
            gui.addItem(food)
        }
        return gui
    }

    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (player.gameMode == GameMode.CREATIVE && isShift) {
            player.inventory.addItem(item)
        } else {}
    }
}
