package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.CookGame.Cookwares.ChoppingBoard
import com.github.AoRingoServer.GUIs
import com.github.AoRingoServer.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodMenu(private val plugin: Plugin) : GUIs {
    override val guiName: String = "${ChatColor.DARK_BLUE}メニュー"
    private val foodManager = FoodManager(plugin)
    private val cookingMap = mapOf(
        "cut" to ChoppingBoard(plugin).knifeItem
    )
    override fun make(player: Player): Inventory {
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
        } else {
            val gui = makeRecipeGUI(item) ?: return
            player.openInventory(gui)
        }
    }
    private fun makeRecipeGUI(finishedProduct: ItemStack): Inventory? {
        val guiSize = 9
        val arrow = ItemManager().make(Material.PAPER, "${ChatColor.YELLOW}→", customModelData = 1)
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        val finishedProductID = foodManager.acquisitionFoodID(finishedProduct) ?: return null
        val cookingFullKey = foodManager.acquisitionCookingMethodKey(finishedProductID) ?: return null
        val parts = cookingFullKey.split(".")
        val cookingKey = parts[0]
        val materialID = parts[2]
        val materialFoodInfo = foodManager.makeFoodInfo(materialID)
        val materialItem = foodManager.makeFoodItem(materialFoodInfo)
        val cookingItem = cookingMap[cookingKey] ?: return null
        gui.setItem(1, materialItem)
        gui.setItem(2, cookingItem)
        gui.setItem(4, arrow)
        gui.setItem(6, finishedProduct)
        return gui
    }
}
