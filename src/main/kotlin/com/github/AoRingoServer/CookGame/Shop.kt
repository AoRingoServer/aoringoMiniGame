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
        val shopFile = Yml(plugin).acquisitionYml("", "shop")
        val shopItemList = shopFile.getMapList("itemsList") as Map<String, String>
        val commercialProductItemList = makeCommercialProductItemList(shopItemList)
        val guiSize = GUIManager().autoGUISize(commercialProductItemList)
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        for (foodID in shopItemList) {
            val foodInfo = foodManager.makeFoodInfo(foodID)
            val price = foodInfo.price
            val foodItem = foodManager.makeFoodItem(foodInfo)
            val meta = foodItem.itemMeta
            meta?.lore = mutableListOf("${price}円")
            foodItem.setItemMeta(meta)
            gui.addItem(foodItem)
        }
        return gui
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
