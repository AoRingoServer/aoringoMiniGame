package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.GUI
import com.github.AoRingoServer.GUIManager
import com.github.AoRingoServer.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Shop(private val plugin: Plugin) : GUI {
    private val foodManager = FoodManager(plugin)
    private val itemManager = ItemManager()
    override val guiName: String = "${ChatColor.DARK_BLUE}ショップ"
    private val foodKey = "food"
    override fun make(player: Player): Inventory {
        val shopFile = Yml(plugin).acquisitionYml("", "shopCommercialProductList")
        val commercialProductList = mutableListOf<ItemStack>()
        for (key in shopFile.getKeys(true)) {
            val list = shopFile.getList(key) as List<String>
            when (key) {
                foodKey -> acquisitionFoodItem(list, commercialProductList)
            }
        }
        val guiSize = GUIManager().autoGUISize(commercialProductList)
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        for (item in commercialProductList) {
            gui.addItem(item)
        }
        return gui
    }
    private fun acquisitionFoodItem(list: List<String>, commercialProductList: MutableList<ItemStack>) {
        for (foodID in list) {
            val foodInfo = foodManager.makeFoodInfo(foodID)
            val foodItem = foodManager.makeFoodItem(foodInfo)
            val meta = foodItem.itemMeta
            meta?.lore = mutableListOf("${ChatColor.GOLD}値段：${foodInfo.price}円")
            foodItem.setItemMeta(meta)
            commercialProductList.add(foodItem)
        }
    }
    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (item.type != Material.MELON_SLICE) { return }
        val salesManager = SalesManager(plugin)
        val foodID = foodManager.acquisitionFoodID(item) ?: return
        val foodInfo = foodManager.makeFoodInfo(foodID)
        val price = foodInfo.price
        val foodItem = foodManager.makeFoodItem(foodInfo)
        val possessionMoney = salesManager.acquisitionPossessionGold(player)
        if (possessionMoney < price) {
            AoringoPlayer(player).sendErrorMessage("所持金が足りません")
            return
        }
        player.inventory.addItem(foodItem)
        salesManager.reduce(player, foodInfo)
        player.sendMessage("${ChatColor.GOLD}商品購入")
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
