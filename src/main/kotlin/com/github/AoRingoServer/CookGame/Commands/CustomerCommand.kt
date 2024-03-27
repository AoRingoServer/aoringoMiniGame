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
        return when (val size = args.size) {
            0 -> mutableListOf()
            1 -> subCommandMap.keys.toMutableList()
            else -> {
                val subCommand = args[0]
                tabReinforcementMap[subCommand]?.invoke(sender, size) ?: mutableListOf()
            }
        }
    }
    private fun summon(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) { return }
        val mode = args[1]
        val coordinateSpecifiedSize = 6
        val location = if (args.size == coordinateSpecifiedSize) { makeLocation(sender, args) } else { acquisitionLocation(sender) } ?: return
        customerManager.summonCustomor(location, mode)
    }
    private val subCommandMap = mapOf<String, (sender: CommandSender, args: Array<out String>) -> Unit>(
        "summon" to { sender, args -> summon(sender, args) }
    )
    private val tabReinforcementMap = mapOf<String, (sender: CommandSender, size: Int) -> MutableList<String>>(
        "summon" to { sender, size -> summonTabReinforcement(size, sender) }
    )
    private fun summonTabReinforcement(size: Int, sender: CommandSender): MutableList<String> {
        val location = acquisitionLocation(sender)
        return when (size) {
            1 -> subCommandMap.keys.toMutableList()
            2 -> mutableListOf("[level]")
            3 -> mutableListOf("${location?.x?.toInt()}")
            4 -> mutableListOf("${location?.y?.toInt()}")
            5 -> mutableListOf("${location?.z?.toInt()}")
            6 -> mutableListOf("[yaw]", "0", "90", "180", "270")
            else -> mutableListOf()
        }
    }
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
