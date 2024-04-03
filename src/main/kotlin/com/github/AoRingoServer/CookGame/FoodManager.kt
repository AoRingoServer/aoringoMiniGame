package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.CookGame.DataClasses.CookGameItemInfo
import com.github.AoRingoServer.Datas.NBT
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.ItemManager
import com.github.AoRingoServer.PluginData
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FoodManager(private val plugin: Plugin) {
    private val nbt = NBT(plugin)
    private val yml = Yml(plugin)
    private val itemManager = ItemManager()
    private val key = itemManager.cookGameItemIDKey
    val foodInfoFile = PluginData.DataManager.foodInfo
    private val cookgameItemManager = CookGameItemManager(plugin)
    fun foodInfoKeyList(): MutableList<String> {
        return foodInfoFile?.getKeys(false)?.toMutableList() ?: mutableListOf()
    }
    fun setFinishedProductList(): MutableList<String> {
        val finishedProductList = mutableListOf<String>()
        for (key in acquireFinishedProduct().getKeys(false)) {
            val list = acquireFinishedProduct().getList(key) as MutableList<String>
            finishedProductList.addAll(list)
        }
        return finishedProductList
    }

    fun makeFoodInfo(foodID: String): CookGameItemInfo? {
        foodInfoFile ?: return null
        return cookgameItemManager.makeInfo(foodID, foodInfoFile, Material.MELON_SLICE)
    }
    fun acquisitionFoodID(food: ItemStack): String? {
        return nbt.acquisition(food, key)
    }
    fun acquireFinishedProduct(): YamlConfiguration {
        return yml.acquisitionYml("", "FinishedProductList")
    }
    fun makeFoodItem(cookGameItemInfo: CookGameItemInfo): ItemStack {
        return cookgameItemManager.makeItem(cookGameItemInfo)
    }
    fun acquisitionCookingCompletionGoodsData(food: ItemStack, method: String): ItemStack? {
        val foodID = acquisitionFoodID(food) ?: return null
        val completionGoodsId = acquisitionCompletionGoodsId(foodID, method) ?: return null
        val completionGoodsFoodInfo = makeFoodInfo(completionGoodsId) ?: return null
        return makeFoodItem(completionGoodsFoodInfo)
    }
    fun acquisitionCookingCompletionGoodsData(foods: MutableList<ItemStack>, method: String): ItemStack? {
        val foodIDList = mutableListOf<String>()
        for (food in foods) {
            val foodID = acquisitionFoodID(food) ?: return null
            foodIDList.add(foodID)
        }
        val completionGoodsId = acquisitionCompletionGoodsId(foodIDList, method) ?: return null
        val completionGoodsFoodInfo = makeFoodInfo(completionGoodsId) ?: return null
        return makeFoodItem(completionGoodsFoodInfo)
    }
    private fun acquisitionCompletionGoodsId(ingredientId: String, method: String): String? {
        val cutCookingData = yml.acquisitionCookingMethodData(method) ?: return null
        return yml.acquisitionKey(cutCookingData, ingredientId)
    }
    private fun acquisitionCompletionGoodsId(ingredientIds: MutableList<String>, method: String): String? {
        val cutCookingData = yml.acquisitionCookingMethodData(method) ?: return null
        return yml.acquisitionKey(cutCookingData, ingredientIds.toSet())
    }
}
