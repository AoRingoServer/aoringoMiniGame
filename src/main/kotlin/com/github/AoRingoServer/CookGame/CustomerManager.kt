package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.Datas.NBT
import com.github.AoRingoServer.Datas.Yml
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
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
    private val yml = Yml(plugin)
    private val cookGameConfig = yml.acquisitionYml("", "cookGameConfig")
    private val bonus = cookGameConfig.getInt("bonus")
    private val chip = cookGameConfig.getInt("chip")
    private val max = cookGameConfig.getInt("max")
    private val nbt = NBT(plugin)
    private val customorInfoKey = "customInfo"

    fun summon(sender: CommandSender, args: Array<out String>) {
        val location = acquisitionLocation(sender, args) ?: return
        summonCustomor(location)
    }
    private fun summonCustomor(location: Location) {
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
        villager.scoreboardTags.add(customerTag)
        villager.isInvulnerable = true
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
        val price = acquisitionproductsPrice(recipe) ?: return
        continuousBonus(recipeCount, aoringoPlayer)
        salesManager.addition(price, aoringoPlayer)
        if (customorRecipManager.isRecipeCountMax(villager, max)) {
            customorReplacement(villager, aoringoPlayer)
        }
    }
    fun newTradingPreparation(villager: Villager) {
        additionalUpdateRecipe(villager)
    }
    private fun customorReplacement(villager: Villager, aoringoPlayer: AoringoPlayer) {
        val player = aoringoPlayer.player
        villager.remove()
        summonCustomor(villager.location)
        player.sendMessage("${ChatColor.YELLOW}お客様は満足して帰宅していった(チップ +$chip)")
        salesManager.addition(chip, aoringoPlayer)
        player.sendMessage("${ChatColor.AQUA}新しいお客さんが入ってきた")
    }
    private fun continuousBonus(count: Int, aoringoPlayer: AoringoPlayer) {
        val player = aoringoPlayer.player
        val delimiter = 5
        val optionCount = 2
        if ((count - optionCount) % delimiter != 0 && count != optionCount) {
            return
        }
        salesManager.addition(bonus, aoringoPlayer)
        player.sendMessage("${ChatColor.AQUA}ボーナス獲得 +$bonus")
        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f)
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
        val orderPaper = customorRecipManager.makeOrderPaper(additionalRecipe)
        recipes.add(customorRecipManager.makeParfaitRecipeMerchantRecipe())
        recipes.add(orderPaper)
        recipes.add(customorRecipManager.makeMerchantRecipe(additionalRecipe))
        customorRecipManager.setTrading(villager, recipes)
    }
    private fun additionalUpdateRecipe(villager: Villager) {
        val additionalRecipe = customorRecipManager.acquisitionCompletionGoodsID() ?: return
        val recipe = customorRecipManager.makeMerchantRecipe(additionalRecipe)
        val orderPaper = customorRecipManager.makeOrderPaper(additionalRecipe)
        customorRecipManager.additionalTrading(villager, recipe, orderPaper)
    }
    fun setCustomorInfo(villager: Villager, value: String?) {
        nbt.set(villager, customorInfoKey, value)
    }
    fun acquisitionCustomorInfo(villager: Villager): String? {
        return nbt.acquisition(villager, customorInfoKey)
    }
}
