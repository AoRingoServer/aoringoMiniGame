package com.github.AoRingoServer.CookGame.Commands

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.CookGame.FoodManager
import com.github.AoRingoServer.CookGame.SalesManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class CookGameCommand(private val plugin: Plugin) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) { return false }
        val subCommand = args[0]
        return subCommands()[subCommand]?.invoke(args, sender) ?: false
    }
    private fun subCommands(): Map<String, (args: Array<out String>, sender: CommandSender) ->Boolean> {
        return mapOf(
            "giveFood" to { args: Array<out String>, sender: CommandSender ->
                val foodManager = FoodManager(plugin)
                if (args.size == 2 && sender is Player) {
                    val input = args[1]
                    val foodInfo = foodManager.makeFoodInfo(input)
                    if (foodInfo != null) {
                        val foodItem = foodManager.makeFoodItem(foodInfo)
                        sender.inventory.addItem(foodItem)
                    }
                }
                true
            },
            "buy" to { args: Array<out String>, sender: CommandSender ->
                if (args.size >= 4 && sender is BlockCommandSender) {
                    val price = args[1].toInt()
                    buyTeamItem(price, sender, args)
                }
                true
            }
        )
    }
    private fun tabReplenishment(subCommand: String, size: Int): MutableList<String> {
        return when (subCommand) {
            "giveFood" -> FoodManager(plugin).foodInfoKeyList()
            "buy" -> buyTab(size)
            else -> mutableListOf()
        }
    }
    private fun buyTab(size: Int): MutableList<String> {
        return when (size) {
            2 -> mutableListOf("[値段]")
            3 -> mutableListOf("[使い切り]", "true", "false")
            else -> mutableListOf()
        }
    }
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        return when (args.size) {
            1 -> subCommands().keys.toMutableList()
            else -> {
                val subCommand = args[0]
                tabReplenishment(subCommand, args.size)
            }
        }
    }
    private fun buyTeamItem(price: Int, commandBlock: BlockCommandSender, args: Array<out String>): Boolean {
        val location = commandBlock.block.location
        val closePlayer = acquisitionClosePlayer(location) ?: return false
        val aoringoPlayer = AoringoPlayer(closePlayer)
        val salesManager = SalesManager(plugin)
        val playerPossessionGold = salesManager.acquisitionPossessionGold(closePlayer)
        val isDisposable = args[2].toBooleanStrict()
        if (playerPossessionGold < price) {
            aoringoPlayer.sendErrorMessage("所持金が足りませんでした")
            return false
        }
        salesManager.reduce(closePlayer, price)
        closePlayer.playSound(closePlayer, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        var command = ""
        for (i in 3 until args.size) {
            command += "${args[i]} "
        }
        commandBlock.server.dispatchCommand(commandBlock, command)
        if (isDisposable) {
            commandBlock.block.type = Material.BEDROCK
        }
        return true
    }
    private fun acquisitionClosePlayer(location: Location): Player? {
        var nearestPlayer: Player? = null
        var nearestDistanceSquared = Double.MAX_VALUE
        val world = location.world
        if (world?.players?.size == 0) { return null }

        for (player in (world?.players)!!) {
            val playerLocation = player.location
            val distanceSquared = location.distanceSquared(playerLocation)
            if (distanceSquared < nearestDistanceSquared) {
                nearestPlayer = player
                nearestDistanceSquared = distanceSquared
            }
        }

        return nearestPlayer
    }
}
