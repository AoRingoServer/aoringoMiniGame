package com.github.AoRingoServer.CookGame

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

class Customer {
    private val name = "${ChatColor.YELLOW}お客様"
    private fun summon(location: Location) {
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
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
    private fun coordinateSpecified(location: Location, args: Array<out String>): Location? {
        val x = args[1].toDouble()
        val y = args[2].toDouble()
        val z = args[3].toDouble()
        val world = location.world
        return Location(world, x, y, z)
    }
    fun makeTradeRecipe(tradeItem:ItemStack,paymentItem:ItemStack): MerchantRecipe {
        val trade = MerchantRecipe(tradeItem,
            0
        )
        trade.addIngredient(paymentItem)
        return trade
    }
    fun setTrading(villager: Villager,tradeList: MutableList<MerchantRecipe>){
        villager.recipes = tradeList
    }
}
