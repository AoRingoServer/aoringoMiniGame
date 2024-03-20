package com.github.AoRingoServer

import com.github.AoRingoServer.Datas.Yml
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Teleporter(private val plugin: Plugin) {
    private val lobbyWorldName = "world"
    fun teleport(player: Player) {
        val world = player.world.name
        val blockID = player.location.clone().add(0.0, -1.0, 0.0).block.type.toString()
        val acquisitionWorldName = acquisitionWorldName(blockID)
        val teleportTargetWorld = if (world == lobbyWorldName) { acquisitionWorldName } else { lobbyWorldName }
        val teleportLocation = Bukkit.getWorld(teleportTargetWorld ?: lobbyWorldName)?.spawnLocation ?: return
        player.teleport(teleportLocation)
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
    private fun acquisitionWorldName(blockID: String): String? {
        val yml = Yml(plugin)
        val infoFile = yml.acquisitionYml("", "World")
        return infoFile.getString(blockID)
    }
}
