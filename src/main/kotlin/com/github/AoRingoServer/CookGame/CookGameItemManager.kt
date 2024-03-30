package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.Datas.NBT
import com.github.AoRingoServer.ItemManager
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class CookGameItemManager(private val plugin: Plugin) {
    private val itemManager = ItemManager()
    private val nbt = NBT(plugin)
    private val key = itemManager.cookGameItemIDKey
    fun makeInfo(id: String, file: YamlConfiguration, material: Material? = null): CookGameItemInfo {
        val writtenMaterial = file.getString("$id.Material").toString()
        val itemName = file.getString("$id.name").toString()
        val customModelData = file.getInt("$id.customModelData")
        val price = file.getInt("$id.price")
        return CookGameItemInfo(id, material ?: Material.valueOf(writtenMaterial), itemName, price, customModelData)
    }
    fun makeItem(cookGameItemInfo: CookGameItemInfo): ItemStack {
        val item = itemManager.make(cookGameItemInfo.material, cookGameItemInfo.itemName, customModelData = cookGameItemInfo.customModelData)
        nbt.set(item, key, cookGameItemInfo.itemID)
        return item
    }
}
