package com.github.AoRingoServer.CookGame

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
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
            "summonCustomer" to { args: Array<out String>, sender: CommandSender ->
                CustomerManager(plugin).summon(sender, args)
            }
        )
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> subCommands().keys.toMutableList()
            else -> mutableListOf()
        }
    }
}
