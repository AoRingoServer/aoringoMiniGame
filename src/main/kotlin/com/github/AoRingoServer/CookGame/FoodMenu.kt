package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.CookGame.Cookwares.Batter
import com.github.AoRingoServer.CookGame.Cookwares.ChoppingBoard
import com.github.AoRingoServer.CookGame.Cookwares.Coalescence
import com.github.AoRingoServer.CookGame.Cookwares.CookwareManager
import com.github.AoRingoServer.CookGame.Cookwares.Flier
import com.github.AoRingoServer.CookGame.Cookwares.Furnace
import com.github.AoRingoServer.CookGame.Cookwares.Pot
import com.github.AoRingoServer.CookGame.DataClasses.CookGameItemInfo
import com.github.AoRingoServer.GUIs.GUI
import com.github.AoRingoServer.GUIs.GUIManager
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

class FoodMenu(private val plugin: Plugin) : GUI, MultiplePageGUI {
    override val guiName: String = "${ChatColor.DARK_BLUE}メニュー"
    private val foodManager = FoodManager(plugin)
    private val guiManager = GUIManager()
    private val cookwareManager = CookwareManager(plugin)
    private val cookingMap = mapOf(
        "cut" to ChoppingBoard(cookwareManager),
        "fly" to Flier(cookwareManager),
        "bake" to Furnace(cookwareManager),
        "boil" to Pot(cookwareManager),
        "batter" to Batter(cookwareManager),
        "coalescence" to Coalescence(cookwareManager)
    )
    override fun make(player: Player): Inventory {
        return makeMenuGUI(1, player)
    }
    override fun makeMenuGUI(pageNumber: Int, player: Player): Inventory {
        val playerGamemode = player.gameMode
        val foodInfoList = if (playerGamemode == GameMode.CREATIVE) { foodManager.foodInfoKeyList() } else { PluginData.DataManager.finishedProduclist }
        val foodList = mutableListOf<ItemStack>()
        for (foodID in foodInfoList) {
            val foodInfo = foodManager.makeFoodInfo(foodID) ?: return player.openInventory.topInventory
            val foodItem = makeFoodForMenu(foodInfo)
            foodList.add(foodItem)
        }
        return guiManager.makeMultiplePageSupportedGUI(foodList, pageNumber, guiName)
    }
    private fun makeFoodForMenu(cookGameItemInfo: CookGameItemInfo): ItemStack {
        val salesManager = SalesManager(plugin)
        val price = cookGameItemInfo.price
        val sellingPrice = salesManager.sellingPriceCalculating(price)
        val item = foodManager.makeFoodItem(cookGameItemInfo)
        val meta = item.itemMeta
        meta?.lore = mutableListOf("原価：${price}円", "販売価格：${sellingPrice}円")
        item.setItemMeta(meta)
        return item
    }

    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (player.gameMode == GameMode.CREATIVE && isShift) {
            val giveItem = item.clone()
            deletingInfo(giveItem)
            player.inventory.addItem(giveItem)
        } else if (item.type == Material.RED_STAINED_GLASS_PANE) {
            guiManager.changePage(item, player, this)
        } else {
            val gui = makeRecipeGUI(item) ?: return
            player.openInventory(gui)
        }
    }
    private fun deletingInfo(item: ItemStack) {
        val meta = item.itemMeta
        meta?.lore = mutableListOf()
        item.setItemMeta(meta)
    }

    private fun makeRecipeGUI(finishedProduct: ItemStack): Inventory? {
        val guiSize = 18
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
        val foodInfo = foodManager.makeFoodInfo(data) ?: return
        val food = foodManager.makeFoodItem(foodInfo)
        val materials = mutableListOf(food)
        installGUI(gui, materials, cookingMethod, finishedProduct)
    }
    private fun multiDisplay(data: List<*>, gui: Inventory, cookingMethod: String, finishedProduct: ItemStack) {
        val materials = mutableListOf<ItemStack>()
        for (foodID in data) {
            val foodInfo = foodManager.makeFoodInfo(foodID.toString()) ?: return
            materials.add(foodManager.makeFoodItem(foodInfo))
        }
        installGUI(gui, materials, cookingMethod, finishedProduct)
    }
    private fun installGUI(gui: Inventory, materials: MutableList<ItemStack>, cookingMethod: String, finishedProduct: ItemStack) {
        val cookingMethodItem = cookingMap[cookingMethod]?.menuItem ?: return
        val arrow = ItemManager().make(Material.PAPER, "${ChatColor.YELLOW}→", customModelData = 1)
        val cookingMethodSlot = 12
        val arrowSlot = 13
        val finishedProductSlot = 15
        gui.setItem(cookingMethodSlot, cookingMethodItem)
        gui.setItem(arrowSlot, arrow)
        gui.setItem(finishedProductSlot, finishedProduct)
        setIngredient(gui, materials)
    }
    private fun setIngredient(gui: Inventory, materials: MutableList<ItemStack>) {
        for (materialFood in materials) {
            gui.addItem(materialFood)
        }
    }
}
