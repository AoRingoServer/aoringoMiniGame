package com.github.AoRingoServer.Commands

import com.github.AoRingoServer.Server
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
        subCommandMap()[subCommand]?.invoke(args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> subCommandMap().keys.toMutableList()
            else -> mutableListOf()
        }
    }
    private fun subCommandMap(): Map<String, (Array<out String>) -> Any> {
        return mapOf(
            "reloadResourcePack" to {
                ResourcePack(plugin).update()
            },
            "serverstop" to { args: Array<out String> ->
                var countDown: Int? = null
                if (args.size == 2) {
                    try {
                        countDown = args[1].toInt()
                    } catch (e: NumberFormatException) {
                        countDown = null
                    }
                }
                Server(plugin).countDownServerSotp(countDown)
            }
        )
    }
}
