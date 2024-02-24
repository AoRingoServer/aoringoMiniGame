package com.github.AoRingoServer

import com.github.AoRingoServer.Commands.LobbyCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        getCommand("lobby")!!.setExecutor(LobbyCommand())
    }
}
