package com.github.AoRingoServer.Commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LobbyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val lobbyWorld = Bukkit.getWorld("world")
        val lobbySpawnLocation = lobbyWorld?.spawnLocation
        val teleportSound = Sound.ENTITY_ENDERMAN_TELEPORT
        if (sender !is Player) {
            return true
        }
        sender.teleport(lobbySpawnLocation ?: sender.location)
        sender.playSound(sender, teleportSound, 1f, 1f)
        return true
    }
}
