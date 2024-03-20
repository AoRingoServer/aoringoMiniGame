package com.github.AoRingoServer.PlenaryCommands

import com.github.AoRingoServer.Teleporter
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class LobbyCommand(private val plugin: Plugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val teleporter = Teleporter(plugin)
        val lobbyWorld = teleporter.lobbyWorldName
        if (sender !is Player) {
            return true
        }
        teleporter.teleport(sender, lobbyWorld)
        return true
    }
}
