package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.CookGame.Cookwares.Batter
import com.github.AoRingoServer.CookGame.Cookwares.ChoppingBoard
import com.github.AoRingoServer.CookGame.Cookwares.Coalescence
import com.github.AoRingoServer.CookGame.Cookwares.Flier
import com.github.AoRingoServer.CookGame.Cookwares.Furnace
import com.github.AoRingoServer.CookGame.Cookwares.Pot
import com.github.AoRingoServer.GUI
import com.github.AoRingoServer.GUIManager
import com.github.AoRingoServer.ItemManager
import com.github.AoRingoServer.PluginData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodMenu(private val plugin: Plugin) : GUI {
    override val guiName: String = "${ChatColor.DARK_BLUE}メニュー"
    private val foodManager = FoodManager(plugin)
    private val cookingMap = mapOf(
        "cut" to ChoppingBoard(plugin),
        "fly" to Flier(plugin),
        "bake" to Furnace(plugin),
        "boil" to Pot(plugin),
        "batter" to Batter(plugin),
        "coalescence" to Coalescence(plugin)
    )
    override fun make(player: Player): Inventory {
        return makeMenuGUI(1, player)
    }
    private fun makeMenuGUI(pageNumber: Int, player: Player): Inventory {
        val playerGamemode = player.gameMode
        val maxSize = 45
        val foodInfoList = if (playerGamemode == GameMode.CREATIVE) { foodManager.foodInfoKeyList() } else { PluginData.DataManager.finishedProduclist }
        val startNumber = (pageNumber - 1) * maxSize
        if (foodInfoList.size < startNumber) {
            return player.openInventory.topInventory
        }
        val newFoodInfoList = foodInfoList.subList(startNumber, foodInfoList.size - 1)
        val autoSize = GUIManager().autoGUISize(newFoodInfoList)
        val guiSize = if (autoSize < maxSize) { autoSize + 9 } else { 54 }
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        for (n in 0 until maxSize) {
            if (n >= newFoodInfoList.size) { break }
            val foodID = newFoodInfoList[n]
            val foodInfo = foodManager.makeFoodInfo(foodID)
            val food = foodManager.makeFoodItem(foodInfo)
            if (gui.contains(food)) { continue }
            gui.addItem(food)
        }
        selectButton(gui)
        return gui
    }

    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (player.gameMode == GameMode.CREATIVE && isShift) {
            player.inventory.addItem(item)
        } else if (item.type == Material.RED_STAINED_GLASS_PANE) {
            val itemName = item.itemMeta?.displayName ?: return
            try {
                val number = itemName.replace("${ChatColor.GOLD}", "").replace("番目", "").toInt()
                val gui = makeMenuGUI(number, player)
                player.openInventory(gui)
            } catch (e: NumberFormatException) { return }
        } else {
            val gui = makeRecipeGUI(item) ?: return
            player.openInventory(gui)
        }
    }
    private fun selectButton(gui: Inventory) {
        val itemManager = ItemManager()
        val guiNumber = gui.size - 9
        for (i in guiNumber..guiNumber + 8) {
            val number = i - guiNumber + 1
            val button = itemManager.make(Material.RED_STAINED_GLASS_PANE, "${ChatColor.GOLD}${number}番目")
            gui.setItem(i, button)
        }
    }
    private fun makeRecipeGUI(finishedProduct: ItemStack): Inventory? {
        val guiSize = 9
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        val finishedProductID = foodManager.acquisitionFoodID(finishedProduct) ?: return null
        val methodData = acquireCookingMethodData(finishedProductID)
        typeProcessingChange(methodData?.acquisitionData, gui, methodData?.cookingMethod ?: return null, finishedProduct)
        return gui
    }
    private fun acquireCookingMethodData(foodID: String): FoodMenuAcquisitionData? {
        val cookingMethodData = PluginData.DataManager.cookingMethodData ?: return null
        for (cookingMethod in cookingMap.keys) {
            val data = cookingMethodData.get("$cookingMethod.$foodID") ?: continue
            return FoodMenuAcquisitionData(cookingMethod, data)
        }
        return null
    }
    private fun typeProcessingChange(data: Any?, gui: Inventory, cookingMethod: String, finishedProduct: ItemStack) {
        when (data) {
            null -> return
            is String -> singleDisplay(data, gui, cookingMethod, finishedProduct)
            is List<*> -> multiDisplay(data, gui, cookingMethod, finishedProduct)
        }
    }
    private fun singleDisplay(data: String, gui: Inventory, cookingMethod: String, finishedProduct: ItemStack) {
        val foodInfo = foodManager.makeFoodInfo(data)
        val food = foodManager.makeFoodItem(foodInfo)
        installGUI(gui, food, cookingMethod, finishedProduct)
    }
    private fun multiDisplay(data: List<*>, gui: Inventory, cookingMethod: String, finishedProduct: ItemStack) {
        val additionFoodID = data[0].toString()
        val foundationFoodID = data[1].toString()
        val additionFoodInfo = foodManager.makeFoodInfo(additionFoodID)
        val foundationFoodInfo = foodManager.makeFoodInfo(foundationFoodID)
        val additionFood = foodManager.makeFoodItem(additionFoodInfo)
        val foundationFood = foodManager.makeFoodItem(foundationFoodInfo)
        installGUI(gui, additionFood, cookingMethod, finishedProduct, foundationFood)
    }
    private fun installGUI(gui: Inventory, leftFood: ItemStack, cookingMethod: String, finishedProduct: ItemStack, rightFood: ItemStack = ItemStack(Material.AIR)) {
        val cookingMethodItem = cookingMap[cookingMethod]?.menuItem ?: return
        val arrow = ItemManager().make(Material.PAPER, "${ChatColor.YELLOW}→", customModelData = 1)
        val leftFoodSlot = 2
        val rightFoodSlot = 3
        val cookingMethodSlot = 4
        val arrowSlot = 5
        val finishedProductSlot = 7
        gui.setItem(rightFoodSlot, rightFood)
        gui.setItem(leftFoodSlot, leftFood)
        gui.setItem(cookingMethodSlot, cookingMethodItem)
        gui.setItem(arrowSlot, arrow)
        gui.setItem(finishedProductSlot, finishedProduct)
    }
}
