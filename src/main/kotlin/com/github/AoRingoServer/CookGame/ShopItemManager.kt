package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.Datas.Yml
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ShopItemManager(private val plugin: Plugin) {
    private val yml = Yml(plugin)
    private val file = yml.acquisitionYml("", "ItemInfo")
    fun makeShopItemInfo(itemID: String): ShopItemInfo {
        val material = file.getString("$itemID.Material").toString()
        val itemName = file.getString("$itemID.ItemName").toString()
        val price = file.getInt("$itemID.price")
        val customModelData = file.getInt("$itemID.CustomModelData")
        return ShopItemInfo(Material.valueOf(material), itemName, price, customModelData)
    }
    fun makeShopItem(shopItemInfo: ShopItemInfo): ItemStack {
        val item = ItemStack(shopItemInfo.material)
        val meta = item.itemMeta
        meta?.setDisplayName(shopItemInfo.itemName)
        meta?.setCustomModelData(shopItemInfo.customModelData)
        item.setItemMeta(meta)
        return item
    }
}
