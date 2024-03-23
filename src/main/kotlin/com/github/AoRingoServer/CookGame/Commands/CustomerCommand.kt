package com.github.AoRingoServer.CookGame.Commands

import com.github.AoRingoServer.CookGame.Customer.CustomerManager
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class CustomerCommand(private val plugin: Plugin) : CommandExecutor, TabExecutor {
    private val customerManager = CustomerManager(plugin)
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val subCommand = args[0]
        if (!subCommandMap.keys.contains(subCommand)) { return false }
        subCommandMap[subCommand]?.invoke(sender, args)
        return true
    }
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return mutableListOf()
    }
    private fun summon(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) { return }
        val mode = args[1].toInt()
        val location = if (args.size == 6) { makeLocation(sender, args) } else { acquisitionLocation(sender) } ?: return
        customerManager.summonCustomor(location, mode)
    }
    private val subCommandMap = mapOf<String, (sender: CommandSender, args: Array<out String>) -> Unit>(
        "summon" to { sender, args -> summon(sender, args) }
    )
    private fun acquisitionLocation(sender: CommandSender): Location? {
        return when (sender) {
            is Player -> sender.location
            is BlockCommandSender -> sender.block.location.clone().add(0.5, 1.0, 0.5)
            else -> null
        }
    }
    private fun makeLocation(sender: CommandSender, args: Array<out String>): Location {
        val world = acquisitionLocation(sender)?.world
        val x = args[2].toDouble()
        val y = args[3].toDouble()
        val z = args[4].toDouble()
        val yaw = args[5].toFloat()
        return Location(world, x + 0.5, y, z + 0.5, yaw, 0f)
    }
}
