package com.github.AoRingoServer.CookGame.Commands

import com.github.AoRingoServer.CookGame.FoodManager
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
        subCommands()[subCommand]?.invoke(args, sender)
        return true
    }
    private fun subCommands(): Map<String, (args: Array<out String>, sender: CommandSender) ->Unit> {
        return mapOf(
            "giveFood" to { args: Array<out String>, sender: CommandSender ->
                val foodManager = FoodManager(plugin)
                if (args.size == 2 && sender is Player) {
                    val input = args[1]
                    val foodInfo = foodManager.makeFoodInfo(input)
                    val foodItem = foodManager.makeFoodItem(foodInfo)
                    sender.inventory.addItem(foodItem)
                }
            }
        )
    }
    private fun tabReplenishment(subCommand: String): MutableList<String> {
        return when (subCommand) {
            "giveFood" -> FoodManager(plugin).foodInfoKeyList()
            else -> mutableListOf()
        }
    }
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> subCommands().keys.toMutableList()
            2 -> {
                val subCommand = args[0]
                tabReplenishment(subCommand)
            }
            else -> mutableListOf()
        }
    }
}
