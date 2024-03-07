package com.github.AoRingoServer

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.awt.TextComponent

class AoringoPlayer(val player: Player) {
    private val adminPermissionName = "aoringoserver.admin"
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
}
