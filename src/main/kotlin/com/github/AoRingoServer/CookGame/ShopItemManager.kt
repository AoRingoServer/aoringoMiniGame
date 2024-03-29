package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.Datas.NBT
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.ItemManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ShopItemManager(private val plugin: Plugin) {
    private val yml = Yml(plugin)
    private val nbt = NBT(plugin)
    val file = yml.acquisitionYml("", "ItemInfo")
    fun makeShopItemInfo(itemID: String): ShopItemInfo {
        val material = file.getString("$itemID.Material").toString()
        val itemName = file.getString("$itemID.ItemName").toString()
        val price = file.getInt("$itemID.price")
        val customModelData = file.getInt("$itemID.CustomModelData")
        return ShopItemInfo(itemID, Material.valueOf(material), itemName, price, customModelData)
    }
    fun makeShopItem(shopItemInfo: ShopItemInfo): ItemStack {
        val itemManager = ItemManager()
        val key = itemManager.cookGameItemIDKey
        val item = itemManager.make(shopItemInfo.material, shopItemInfo.itemName, customModelData = shopItemInfo.customModelData)
        nbt.set(item, key, shopItemInfo.itemID)
        return item
    }
}
