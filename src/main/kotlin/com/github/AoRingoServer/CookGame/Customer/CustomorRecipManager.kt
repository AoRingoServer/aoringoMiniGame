package com.github.AoRingoServer.CookGame.Customer

import com.github.AoRingoServer.CookGame.DataClasses.FoodInfo
import com.github.AoRingoServer.CookGame.FoodManager
import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class CustomorRecipManager(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    private val itemManager = ItemManager()

    val dirtyTrayName = "${ChatColor.YELLOW}少し汚れたおぼん"
    val tray = itemManager.make(Material.BOWL, "${ChatColor.GOLD}おぼん", customModelData = 3)
    fun determineOrderFood(villager: Villager): ItemStack {
        val level = villager.villagerLevel
        val key = "level$level"
        val foodList = foodManager.acquireFinishedProduct().getList(key)
        val foodID = foodList?.random().toString()
        val foodInfo = foodManager.makeFoodInfo(foodID)
        return foodManager.makeFoodItem(foodInfo)
    }
    fun makeTray(foodInfo: FoodInfo): ItemStack {
        val customModelData = 4
        val price = foodInfo.price
        val lore = mutableListOf("金額：${price}円")
        return itemManager.make(Material.BOWL, dirtyTrayName, lore = lore, customModelData = customModelData)
    }
}
