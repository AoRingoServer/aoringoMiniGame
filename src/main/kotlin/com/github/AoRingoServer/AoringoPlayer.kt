package com.github.AoRingoServer

import com.github.AoRingoServer.GUIs.ServerMenu
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.awt.TextComponent

class AoringoPlayer(val player: Player) {
    private val adminPermissionName = "aoringoserver.admin"
    val teamName: String? = player.scoreboard.teams.firstOrNull { it.hasEntry(player.name) }?.name
    val scoreboardTargetName = teamName ?: player.name
    fun setPrefix() {
        when {
            checkAdmin() -> changeName("${ChatColor.YELLOW}[運営]")
        }
    }
    private fun checkAdmin(): Boolean {
        return player.hasPermission(adminPermissionName)
    }
    private fun changeName(prefix: String) {
        player.setDisplayName("$prefix ${ChatColor.WHITE}${player.name}")
        player.setPlayerListName(("$prefix ${ChatColor.WHITE}${player.name}"))
    }
    fun sendActionBar(title: String) {
        val actionBarMessage = ChatColor.translateAlternateColorCodes('&', title)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent(actionBarMessage))
    }
    fun sendErrorMessage(message: String) {
        player.sendMessage("${ChatColor.RED}$message")
        player.playSound(player, Sound.BLOCK_BELL_USE, 1f, 1f)
    }
    fun giveServerMenuItem(plugin: Plugin) {
        val serverMenu = ServerMenu(plugin)
        val serverMenuItem = serverMenu.item
        if (!player.inventory.contains(serverMenuItem)) {
            player.inventory.addItem(serverMenuItem)
        }
    }
}
