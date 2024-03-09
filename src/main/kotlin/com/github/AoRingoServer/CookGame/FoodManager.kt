package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.ItemManager
import com.github.Ringoame196.Yml
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodManager(private val plugin: Plugin) {
    private val itemManager = ItemManager()
    fun make(id: String): ItemStack {
        val foodInfoData = Yml(plugin).getYml("", "FoodInfo")
        val name = foodInfoData.getString("$id.name") ?: "未設定"
        val customModelData = foodInfoData.getInt("$id.customModelData")
        return itemManager.make(Material.MELON_SLICE, name, customModelData = customModelData)
    }
}
