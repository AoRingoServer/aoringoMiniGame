package com.github.AoRingoServer

import com.github.AoRingoServer.Datas.Yml
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Teleporter(private val plugin: Plugin) {
    val lobbyWorldName = "world"
    fun sneakTeleport(player: Player) {
        val world = player.world.name
        val blockID = player.location.clone().add(0.0, -1.0, 0.0).block.type.toString()
        val acquisitionWorldName = acquisitionWorldName(blockID)
        val teleportTargetWorld = if (world == lobbyWorldName) { acquisitionWorldName } else { lobbyWorldName } ?: return
        teleport(player, teleportTargetWorld)
    }
    private fun acquisitionWorldName(blockID: String): String? {
        val yml = Yml(plugin)
        val infoFile = yml.acquisitionYml("", "World")
        return infoFile.getString(blockID)
    }
    fun teleport(player: Player, teleportWorldName: String) {
        val location = Bukkit.getWorld(teleportWorldName)?.spawnLocation ?: return
        player.teleport(location)
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
}
