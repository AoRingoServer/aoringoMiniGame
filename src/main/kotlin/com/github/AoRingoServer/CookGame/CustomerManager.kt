package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin

class CustomerManager(private val plugin: Plugin) {
    private val name = "${ChatColor.YELLOW}お客様"
    val customerTag = "cookGameCustomer"
    val customorRecipManager = CustomorRecipManager(plugin)
    private val salesManager = SalesManager(plugin)

    fun summon(sender: CommandSender, args: Array<out String>) {
        val location = acquisitionLocation(sender, args) ?: return
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
        villager.scoreboardTags.add(customerTag)
        customorRecipManager.setDefaultRecipe(villager)
    }
    private fun acquisitionLocation(sender: CommandSender, args: Array<out String>): Location? {
        val sizeAtCoordinateInput = 4
        val commandSenderLocation = acquisitionCommandSenderLocation(sender) ?: return null
        return if (args.size == sizeAtCoordinateInput) {
            coordinateSpecified(commandSenderLocation, args)
        } else {
            commandSenderLocation
        }
    }
    private fun acquisitionCommandSenderLocation(sender: CommandSender): Location? {
        return when (sender) {
            is Player -> sender.location
            is BlockCommandSender -> sender.block.location.clone().add(0.5, 2.0, 0.5)
            else -> return null
        }
    }
    private fun coordinateSpecified(location: Location, args: Array<out String>): Location? {
        val x = args[1].toDouble()
        val y = args[2].toDouble()
        val z = args[3].toDouble()
        val world = location.world
        return Location(world, x, y, z)
    }
    fun takeOrder(villager: Villager, recipe: ItemStack, aoringoPlayer: AoringoPlayer, recipeCount: Int) {
        additionalUpdateRecipe(villager)
        val productsPrice = acquisitionproductsPrice(recipe) ?: return
        val price = additionalSalesCalculating(productsPrice, recipeCount)
        salesManager.addition(price, aoringoPlayer)
    }
    private fun additionalUpdateRecipe(villager: Villager) {
        val additionalRecipe = customorRecipManager.acquisitionCompletionGoodsID() ?: return
        val recipe = customorRecipManager.makeMerchantRecipe(additionalRecipe)
        customorRecipManager.additionalTrading(villager, recipe)
    }
    private fun additionalSalesCalculating(productsPrice: Int, count: Int): Int {
        val additionalSalesRate = 1.5
        return if (count >= 5) {
            (productsPrice * additionalSalesRate).toInt()
        } else {
            productsPrice
        }
    }
    private fun acquisitionproductsPrice(recipe: ItemStack): Int? {
        val loreNumber = 1
        val priceDisplay = recipe.itemMeta?.lore?.get(loreNumber) ?: return null
        val priceString = priceDisplay.replace("金額：", "").replace("円", "")
        return priceString.toInt()
    }
    fun acquisitionCustomer(inventory: Inventory): Villager? {
        val villager = inventory.holder as? Villager ?: return null
        return if (villager.scoreboardTags.contains(customerTag)) {
            villager
        } else {
            null
        }
    }
    fun skipTrade(villager: Villager) {
        val additionalRecipe = customorRecipManager.acquisitionCompletionGoodsID() ?: return
        val recipes = mutableListOf<MerchantRecipe>()
        recipes.add(customorRecipManager.makeParfaitRecipeMerchantRecipe())
        recipes.add(customorRecipManager.makeMerchantRecipe(additionalRecipe))
        customorRecipManager.setTrading(villager, recipes)
    }
}
