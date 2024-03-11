package com.github.AoRingoServer.CookGame

import com.github.Ringoame196.Yml
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class CustomerManager(private val plugin: Plugin) {
    private val name = "${ChatColor.YELLOW}お客様"
    val customerTag = "cookGameCustomer"
    val customorRecipManager = CustomorRecipManager(plugin)
    fun summon(sender: CommandSender, args: Array<out String>) {
        val location = acquisitionLocation(sender,args)?:return
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
        villager.scoreboardTags.add(customerTag)
        setDefaultRecipe(villager)
    }
    private fun acquisitionLocation(sender: CommandSender, args: Array<out String>):Location?{
        val commandSenderLocation = acquisitionCommandSenderLocation(sender) ?:return null
        return if (args.size == 4){
            coordinateSpecified(commandSenderLocation, args)
        } else {
            commandSenderLocation
        }
    }
    private fun acquisitionCommandSenderLocation(sender: CommandSender):Location?{
        return when(sender){
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
    fun reduceMaterial(inventory: Inventory) {
        val materialSlot = when {
            inventory.getItem(0) != null -> 0
            inventory.getItem(1) != null -> 1
            else -> return
        }
        val materialItem = inventory.getItem(materialSlot)?.clone() ?: return
        val amount = materialItem.amount
        materialItem.amount = amount - 1
        inventory.setItem(materialSlot, materialItem)
    }
    fun takeOrder(villager: Villager) {
        val additionalRecipe = acquisitionCompletionGoodsID() ?: return
        val recipe = customorRecipManager.makeMerchantRecipe(additionalRecipe)
        customorRecipManager.additionalTrading(villager, recipe)
    }
    private fun acquisitionCompletionGoodsID(): String? {
        val finishedProductList = Yml(plugin).getList("", "FinishedProductList", "basic") ?: return null
        val size = finishedProductList.size
        val r = Random.nextInt(0, size - 1)
        return finishedProductList[r]
    }
    private fun setDefaultRecipe(villager: Villager) {
        val appetizer = "appetizer"
        val parfaitItem = customorRecipManager.makeParfaitRecipeMerchantRecipe()
        val appetizerItem = customorRecipManager.makeMerchantRecipe(appetizer)
        val tradeRecipe = mutableListOf<MerchantRecipe>()
        tradeRecipe.add(parfaitItem)
        tradeRecipe.add(appetizerItem)
        customorRecipManager.setTrading(villager, tradeRecipe)
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
        val additionalRecipe = acquisitionCompletionGoodsID() ?: return
        val recipes = mutableListOf<MerchantRecipe>()
        recipes.add(customorRecipManager.makeParfaitRecipeMerchantRecipe())
        recipes.add(customorRecipManager.makeMerchantRecipe(additionalRecipe))
        customorRecipManager.setTrading(villager, recipes)
    }
}
