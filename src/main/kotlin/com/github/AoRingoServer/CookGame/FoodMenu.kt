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
    private val itemManager = ItemManager()
    private val cookingMap = mapOf(
        "cut" to ChoppingBoard(plugin).knifeItem,
        "fly" to itemManager.make(Material.CAULDRON, "${ChatColor.YELLOW}揚げる", customModelData = 1),
        "bake" to itemManager.make(Material.SMOKER, "${ChatColor.GOLD}焼く"),
        "boil" to itemManager.make(Material.CAULDRON, "${ChatColor.GOLD}茹でる", customModelData = 3)
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
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        val finishedProductID = foodManager.acquisitionFoodID(finishedProduct) ?: return null
        val cookingFullKey = foodManager.acquisitionCookingMethodKey(finishedProductID) ?: return null
        installationRecipeGUIItem(gui, cookingFullKey, finishedProduct)
        return gui
    }
    private fun installationRecipeGUIItem(gui: Inventory, recipeData: RecipeData, finishedProduct: ItemStack) {
        val arrow = ItemManager().make(Material.PAPER, "${ChatColor.YELLOW}→", customModelData = 1)
        val materialID = recipeData.materialID
        val cookingType = recipeData.cuisineType
        val materialFoodInfo = foodManager.makeFoodInfo(materialID)
        val materialItem = foodManager.makeFoodItem(materialFoodInfo)
        val cookingItem = cookingMap[cookingType] ?: return
        val materialItemSlot = 1
        val cookingTypeSlot = 2
        val arrowSlot = 4
        val finishedProductSlot = 6
        gui.setItem(materialItemSlot, materialItem)
        gui.setItem(cookingTypeSlot, cookingItem)
        gui.setItem(arrowSlot, arrow)
        gui.setItem(finishedProductSlot, finishedProduct)
    }
}
