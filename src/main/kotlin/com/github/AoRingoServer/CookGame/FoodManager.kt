package com.github.AoRingoServer.CookGame

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
    private val foodIDKey = "foodID"
    private val foodInfoFile = PluginData.DataManager.foodInfo
    fun foodInfoKeyList(): MutableList<String> {
        return foodInfoFile?.getKeys(false)?.toMutableList() ?: mutableListOf()
    }
    fun finishedProductList(): MutableList<String> {
        val basicList = finishedProduct().getList("basic")?.mapNotNull { it.toString() } ?: mutableListOf()
        val additionList = finishedProduct().getList("addition")?.mapNotNull { it.toString() } ?: mutableListOf()
        return (basicList + additionList).toMutableList()
    }

    fun makeFoodInfo(foodID: String): FoodInfo {
        val name = foodInfoFile?.getString("$foodID.name") ?: "未設定"
        val customModelData = foodInfoFile?.getInt("$foodID.customModelData") ?: 1
        val price = foodInfoFile?.getInt("$foodID.price") ?: 100
        return FoodInfo(foodID, name, customModelData, price)
    }
    fun acquisitionFoodID(food: ItemStack): String? {
        return nbt.acquisition(food, foodIDKey)
    }
    private fun finishedProduct(): YamlConfiguration {
        return yml.acquisitionYml("", "FinishedProductList")
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
    fun acquisitionCookingCompletionGoodsData(food: ItemStack, method: String): ItemStack? {
        val foodID = acquisitionFoodID(food) ?: return null
        val completionGoodsId = acquisitionCompletionGoodsId(foodID, method) ?: return null
        val completionGoodsFoodInfo = makeFoodInfo(completionGoodsId)
        return makeFoodItem(completionGoodsFoodInfo)
    }
    fun acquisitionCookingCompletionGoodsData(foods: CoalescenceRecipeData, method: String): ItemStack? {
        val addingIngredientId = acquisitionFoodID(foods.addingFood) ?: return null
        val foundationID = acquisitionFoodID(foods.foundationFood) ?: return null
        val list = mutableListOf(addingIngredientId, foundationID)
        val completionGoodsId = acquisitionCompletionGoodsId(list, method) ?: return null
        val completionGoodsFoodInfo = makeFoodInfo(completionGoodsId)
        return makeFoodItem(completionGoodsFoodInfo)
    }
    fun acquisitionCookingMethodKey(foodID: String): RecipeData? {
        val cookingMethodData = Yml(plugin).cookingMethodData()
        val keys = cookingMethodData.getKeys(true)
        for (key in keys) {
            if (!key.contains(".$foodID")) { continue }
            val materialId = cookingMethodData.getString(key)
            val fullKey = "$key.$materialId"
            return makeRecipeData(fullKey)
        }
        return null
    }
    private fun acquisitionCompletionGoodsId(ingredientId: String, method: String): String? {
        val cutCookingData = yml.acquisitionCookingMethodData(method) ?: return null
        return yml.acquisitionKey(cutCookingData, ingredientId)
    }
    private fun acquisitionCompletionGoodsId(ingredientIds: MutableList<String>, method: String): String? {
        val cutCookingData = yml.acquisitionCookingMethodData(method) ?: return null
        return yml.acquisitionKey(cutCookingData, ingredientIds)
    }
    private fun makeRecipeData(fullKey: String): RecipeData {
        val parts = fullKey.split(".")
        val cookingType = parts[0]
        val finishedProductID = parts[1]
        val materialID = parts[2]
        return RecipeData(cookingType, finishedProductID, materialID)
    }
}
