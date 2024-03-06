package com.github.AoRingoServer.Commands

import com.github.Ringoame196.ResourcePack
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.Plugin

class AoringoCommand(private val plugin: Plugin) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) { return false }
        val subCommand = args[0]
        subCommandMap()[subCommand]?.invoke()
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> subCommandMap().keys.toMutableList()
            else -> mutableListOf()
        }
    }
    private fun subCommandMap(): Map<String, () -> Unit> {
        return mapOf(
            "reloadResourcePack" to {
                ResourcePack(plugin).update()
            }
        )
    }
}
