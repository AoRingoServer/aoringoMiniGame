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
        val playerGamemode = player.gameMode
        val foodInfoList = if (playerGamemode == GameMode.CREATIVE) { foodManager.foodInfoKeyList() } else { foodManager.finishedProductList() }
        val guiSize = autoGUISize(foodInfoList)
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        for (foodID in foodInfoList) {
            val foodInfo = foodManager.makeFoodInfo(foodID)
            val food = foodManager.makeFoodItem(foodInfo)
            gui.addItem(food)
        }
        return gui
    }
    private fun autoGUISize(foodInfoList: MutableList<String>): Int {
        val listSize = foodInfoList.size
        val maxSize = 54
        val column = listSize / 9 + 1
        val size = column * 9
        return if (size > maxSize) { maxSize } else { size }
    }

    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (player.gameMode == GameMode.CREATIVE && isShift) {
            player.inventory.addItem(item)
        } else {}
    }
}
