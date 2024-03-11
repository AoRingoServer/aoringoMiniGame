package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.ItemManager
import com.github.Ringoame196.Yml
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodManager(private val plugin: Plugin) {
    fun makeFoodInfo(foodID: String): FoodInfo {
        val yml = acquisitionFoodInfo()
        val name = yml.getString("$foodID.name") ?: "未設定"
        val customModelData = yml.getInt("$foodID.customModelData")
        val price = yml.getInt("$foodID.price")
        return FoodInfo(name, customModelData, price)
    }
    private fun acquisitionFoodInfo(): YamlConfiguration {
        return Yml(plugin).getYml("", "FoodInfo")
    }
    fun makeFoodItem(foodInfo: FoodInfo): ItemStack {
        val itemManager = ItemManager()
        val name = foodInfo.foodName
        val customModelData = foodInfo.customModelData
        return itemManager.make(Material.MELON_SLICE, name, customModelData = customModelData)
    }
}
