package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.Datas.Yml
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ShopItemManager(private val plugin: Plugin) {
    private val yml = Yml(plugin)
    private val cookGameItemManager = CookGameItemManager(plugin)
    val file = yml.acquisitionYml("", "ItemInfo")
    fun makeShopItemInfo(itemID: String): CookGameItemInfo {
        return cookGameItemManager.makeInfo(itemID, file)
    }
    fun makeShopItem(cookGameItemInfo: CookGameItemInfo): ItemStack {
        return cookGameItemManager.makeItem(cookGameItemInfo)
    }
}
