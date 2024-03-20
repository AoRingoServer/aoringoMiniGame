package com.github.AoRingoServer.PlenaryCommands

import com.github.AoRingoServer.AdminBookManager
import com.github.AoRingoServer.Server
import com.github.Ringoame196.ResourcePack
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class AoringoCommand(private val plugin: Plugin) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) { return false }
        val subCommand = args[0]
        subCommandMap()[subCommand]?.invoke(args, sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> subCommandMap().keys.toMutableList()
            else -> mutableListOf()
        }
    }
    private fun subCommandMap(): Map<String, (Array<out String>, CommandSender) -> Any> {
        return mapOf(
            "reloadResourcePack" to { args: Array<out String>, commandSender: CommandSender ->
                ResourcePack(plugin).update()
            },
            "serverstop" to { args: Array<out String>, commandSender: CommandSender ->
                var countDown: Int? = null
                if (args.size == 2) {
                    try {
                        countDown = args[1].toInt()
                    } catch (e: NumberFormatException) {
                        countDown = null
                    }
                }
                Server(plugin).countDownServerSotp(countDown)
            },
            "adminholderchange" to { strings: Array<out String>, commandSender: CommandSender ->
                if (commandSender is Player) {
                    val item = commandSender.inventory.itemInMainHand
                    AdminBookManager(item).adminHolderChange(commandSender)
                }
            }
        )
    }
}
