package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin

class Customer(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    private val name = "${ChatColor.YELLOW}お客様"
    val customerTag = "cookGameCustomer"
    private fun summon(location: Location) {
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
        villager.scoreboardTags.add(customerTag)
        setDefaultRecipe(villager)
    }
    fun commandProcess(sender: CommandSender, args: Array<out String>) {
        val senderLocation = when (sender) {
            is Player -> sender.location
            is BlockCommandSender -> sender.block.location.clone().add(0.5, 2.0, 0.5)
            else -> return
        }
        val location = if (args.size == 4) {
            coordinateSpecified(senderLocation, args)
        } else senderLocation
        summon(location ?: return)
    }
    private fun setDefaultRecipe(villager: Villager) {
        val appetizer = foodManager.make("appetizer")
        val salesItem = makeSalesItemPhysician(100)
        val tradeRecipe = mutableListOf<MerchantRecipe>()
        tradeRecipe.add(makeTradeRecipe(salesItem, appetizer, 1))
        tradeRecipe.add(setParfaitRecipe())
        setTrading(villager, tradeRecipe)
    }
    private fun setParfaitRecipe(): MerchantRecipe {
        val parfait = foodManager.make("parfait")
        val skipItem = ItemManager().make(Material.BARRIER, "${ChatColor.RED}スキップ")
        return makeTradeRecipe(skipItem, parfait, 9999)
    }
    private fun coordinateSpecified(location: Location, args: Array<out String>): Location? {
        val x = args[1].toDouble()
        val y = args[2].toDouble()
        val z = args[3].toDouble()
        val world = location.world
        return Location(world, x, y, z)
    }
    private fun makeTradeRecipe(tradeItem: ItemStack, paymentItem: ItemStack, tradingTimes: Int): MerchantRecipe {
        val trade = MerchantRecipe(
            tradeItem,
            tradingTimes
        )
        trade.addIngredient(paymentItem)
        return trade
    }
    private fun setTrading(villager: Villager, tradeList: MutableList<MerchantRecipe>) {
        villager.recipes = tradeList
    }
    fun increaseSales(amount: Int) {
    }
    private fun makeSalesItemPhysician(price: Int): ItemStack {
        val itemManager = ItemManager()
        val lore = mutableListOf("${price}円")
        return itemManager.make(Material.EMERALD, "${ChatColor.GREEN}売り上げ", lore = lore)
    }
}
