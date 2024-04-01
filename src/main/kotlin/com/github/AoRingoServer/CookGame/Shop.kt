package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.Datas.NBT
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.GUIs.GUI
import com.github.AoRingoServer.GUIs.GUIManager
import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Shop(private val plugin: Plugin) : GUI, MultiplePageGUI {
    private val foodManager = FoodManager(plugin)
    private val shopItemManager = ShopItemManager(plugin)
    override val guiName: String = "${ChatColor.DARK_BLUE}ショップ"
    private val foodKey = "food"
    private val guiManager = GUIManager()
    override fun make(player: Player): Inventory {
        return makeMenuGUI(1, player)
    }

    override fun makeMenuGUI(pageNumber: Int, player: Player): Inventory {
        val shopFile = Yml(plugin).acquisitionYml("", "shopCommercialProductList")
        val commercialProductList = mutableListOf<ItemStack>()
        for (key in shopFile.getKeys(true)) {
            val list = shopFile.getList(key) as List<String>
            when (key) {
                foodKey -> acquisitionFoodItem(list, commercialProductList)
                else -> acquisitionShopItem(list, commercialProductList)
            }
        }
        return guiManager.makeMultiplePageSupportedGUI(commercialProductList, pageNumber, guiName)
    }
    private fun acquisitionFoodItem(list: List<String>, commercialProductList: MutableList<ItemStack>) {
        for (foodID in list) {
            val foodInfo = foodManager.makeFoodInfo(foodID) ?: return
            val foodItem = foodManager.makeFoodItem(foodInfo)
            val meta = foodItem.itemMeta
            meta?.lore = mutableListOf("${ChatColor.GOLD}値段：${foodInfo.price}円")
            foodItem.setItemMeta(meta)
            commercialProductList.add(foodItem)
        }
    }
    private fun acquisitionShopItem(list: List<String>, commercialProductList: MutableList<ItemStack>) {
        for (itemID in list) {
            val itemInfo = shopItemManager.makeShopItemInfo(itemID)
            val shopItem = shopItemManager.makeShopItem(itemInfo)
            val meta = shopItem.itemMeta
            meta?.lore = mutableListOf("${ChatColor.GOLD}値段：${itemInfo.price}円")
            shopItem.setItemMeta(meta)
            commercialProductList.add(shopItem)
        }
    }
    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (item.type == Material.AIR) { return }
        val salesManager = SalesManager(plugin)
        val itemManager = ItemManager()
        val key = itemManager.cookGameItemIDKey
        if (item.type == Material.RED_STAINED_GLASS_PANE) {
            guiManager.changePage(item, player, this)
            return
        }
        val itemID = NBT(plugin).acquisition(item, key) ?: return
        val productsPrice = if (item.type == Material.MELON_SLICE) {
            foodManager.foodInfoFile
        } else {
            shopItemManager.itemInfoData
        }
        val price = productsPrice?.getInt("$itemID.price") ?: return
        val possessionMoney = salesManager.acquisitionPossessionGold(player)
        if (possessionMoney < price) {
            AoringoPlayer(player).sendErrorMessage("所持金が足りません")
            return
        }
        val passingItem = unsetPriceLabel(item)
        player.inventory.addItem(passingItem)
        salesManager.reduce(player, price)
        player.sendMessage("${ChatColor.GOLD}商品購入")
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    private fun unsetPriceLabel(item: ItemStack): ItemStack {
        val meta = item.itemMeta
        meta?.lore = mutableListOf()
        item.clone().setItemMeta(meta)
        return item
    }
}
