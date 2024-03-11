package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.ItemManager
import com.github.Ringoame196.Yml
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class CustomorRecipManager(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    private val itemManager = ItemManager()
    val receiptName = "${ChatColor.YELLOW}レシート"
    val skipItem = itemManager.make(Material.BARRIER, "${ChatColor.RED}スキップ")
    fun makeMerchantRecipe(foodID: String): MerchantRecipe {
        val foodInfoData = foodManager.makeFoodInfo(foodID)
        val food = foodManager.makeFoodItem(foodInfoData)
        val receipt = makeReceipt(foodInfoData)
        return makeTradeRecipe(receipt, food, 1)
    }
    private fun makeReceipt(foodInfo: FoodInfo): ItemStack {
        val itemManager = ItemManager()
        val customModelData = 12
        val price = foodInfo.price
        val lore = mutableListOf("合計金額${price}円")
        return itemManager.make(Material.PAPER, receiptName, lore = lore, customModelData = customModelData)
    }
    private fun makeTradeRecipe(tradeItem: ItemStack, paymentItem: ItemStack, tradingTimes: Int): MerchantRecipe {
        val trade = MerchantRecipe(
            tradeItem,
            tradingTimes
        )
        trade.addIngredient(paymentItem)
        return trade
    }
    fun makeParfaitRecipeMerchantRecipe(): MerchantRecipe {
        val foodID = "parfait"
        val foodManager = FoodManager(plugin)
        val parfaitFoodInfo = foodManager.makeFoodInfo(foodID)
        val item = foodManager.makeFoodItem(parfaitFoodInfo)
        return makeTradeRecipe(skipItem, item, 1)
    }
    fun setTrading(villager: Villager, tradeList: MutableList<MerchantRecipe>) {
        villager.recipes = tradeList
    }
    fun additionalTrading(villager: Villager, recipe: MerchantRecipe) {
        val newRecipes = mutableListOf<MerchantRecipe>()
        newRecipes.addAll(villager.recipes)
        newRecipes.add(recipe)
        setTrading(villager, newRecipes)
    }
    fun acquisitionCompletionGoodsID(): String? {
        val finishedProductList = Yml(plugin).getList("", "FinishedProductList", "basic") ?: return null
        val size = finishedProductList.size
        val r = Random.nextInt(0, size - 1)
        return finishedProductList[r]
    }
    fun setDefaultRecipe(villager: Villager) {
        val appetizer = "appetizer"
        val parfaitItem = makeParfaitRecipeMerchantRecipe()
        val appetizerItem = makeMerchantRecipe(appetizer)
        val tradeRecipe = mutableListOf<MerchantRecipe>()
        tradeRecipe.add(parfaitItem)
        tradeRecipe.add(appetizerItem)
        setTrading(villager, tradeRecipe)
    }
    fun reduceMaterial(inventory: Inventory) {
        val slot0Item = inventory.getItem(0)
        val slot1Item = inventory.getItem(1)
        val materialSlot = when {
            slot0Item != null -> 0
            slot1Item != null -> 1
            else -> return
        }
        val materialItem = inventory.getItem(materialSlot)?.clone() ?: return
        val amount = materialItem.amount
        materialItem.amount = amount - 1
        inventory.setItem(materialSlot, materialItem)
    }
}
