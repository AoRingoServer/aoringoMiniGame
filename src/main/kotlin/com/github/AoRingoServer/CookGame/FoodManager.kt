package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.Datas.NBT
import com.github.AoRingoServer.ItemManager
import com.github.Ringoame196.Yml
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodManager(private val plugin: Plugin) {
    private val nbt = NBT(plugin)
    private val foodIDKey = "foodID"
    fun foodInfoList(): MutableList<String> {
        return acquisitionFoodInfo().getKeys(true).toMutableList()
    }
    fun makeFoodInfo(foodID: String): FoodInfo {
        val yml = acquisitionFoodInfo()
        val name = yml.getString("$foodID.name") ?: "未設定"
        val customModelData = yml.getInt("$foodID.customModelData")
        val price = yml.getInt("$foodID.price")
        return FoodInfo(foodID, name, customModelData, price)
    }
    private fun acquisitionFoodInfo(): YamlConfiguration {
        return Yml(plugin).getYml("", "FoodInfo")
    }
    fun makeFoodItem(foodInfo: FoodInfo): ItemStack {
        val itemManager = ItemManager()
        val name = foodInfo.foodName
        val foodID = foodInfo.foodID
        val customModelData = foodInfo.customModelData
        val food = itemManager.make(Material.MELON_SLICE, name, customModelData = customModelData)
        nbt.set(food, foodIDKey, foodID)
        return food
    }
}
